package com.hadaka_electro.customer.customer;

import com.hadaka_electro.common.entities.Customer;
import com.hadaka_electro.common.entities.setting.Country;
import com.hadaka_electro.common.entities.setting.Setting;
import com.hadaka_electro.common.entities.setting.SettingCategory;
import com.hadaka_electro.common.exception.DuplicatedObjectException;
import com.hadaka_electro.common.exception.ObjectNotFoundException;
import com.hadaka_electro.customer.MailUtility;
import com.hadaka_electro.customer.customer.repository.CustomerDTO;
import com.hadaka_electro.customer.customer.repository.CustomerMapper;
import com.hadaka_electro.customer.customer.repository.CustomerRepository;
import com.hadaka_electro.customer.customer.repository.ResetPasswordRequest;
import com.hadaka_electro.customer.setting.repository.CountryRepository;
import com.hadaka_electro.customer.setting.repository.SettingsRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CountryRepository countryRepository;
    private final PasswordEncoder passwordEncoder;
    private final SettingsRepository settingsRepository;
    private final CustomerMapper customerMapper;

    @Value("${app.frontend.baseUrl}")
    private String frontendBaseUrl;

    public CustomerService(CustomerRepository customerRepository, CountryRepository countryRepository, PasswordEncoder passwordEncoder, SettingsRepository settingsRepository, CustomerMapper customerMapper) {
        this.customerRepository = customerRepository;
        this.countryRepository = countryRepository;
        this.passwordEncoder = passwordEncoder;
        this.settingsRepository = settingsRepository;
        this.customerMapper = customerMapper;
    }

    @Transactional(readOnly = true)
    public List<Country> getAllCountries() {
        return countryRepository.findAllByNameAsc();
    }

    @Transactional
    public void updateCustomer(CustomerDTO customerDTO) {
        Customer customer = customerMapper.toEntity(customerDTO);

        customerRepository.save(customer);
    }

    @Transactional
    public Customer saveCustomer(CustomerDTO customerDTO) {
        Customer customer = customerMapper.toEntity(customerDTO);

        customer.setEnabled(false);
        String verificationCode = RandomString.make(64);
        customer.setVerificationCode(verificationCode);

        return customerRepository.save(customer);
    }

    @Transactional(readOnly = true)
    public Map<String, String> getMailSettings() {
        Map<String, String> mailSettings = settingsRepository
                .findByCategoryOrCategory(SettingCategory.MAIL_SERVER, SettingCategory.MAIL_TEMPLATES)
                .stream().collect(Collectors.toMap(Setting::getKey, Setting::getValue));
        return mailSettings;
    }

    //    @Transactional(readOnly = true) not required cuz,the db will be opened for the duration of email send.
    @Async
    public void sendVerificationEmail(Customer savedCustomer, Map<String, String> mailSettings) throws MessagingException {

        // set mailServer(host), port, username, password, turn on Auth and TLS.
        JavaMailSenderImpl mailSender = MailUtility.prepareMailSender(mailSettings);

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);


        mimeMessageHelper.setFrom(mailSettings.get("MAIL_FROM"));
        mimeMessageHelper.setTo(savedCustomer.getEmail());
        mimeMessageHelper.setSubject(mailSettings.get("CUSTOMER_VERIFY_SUBJECT"));

        String content = mailSettings.get("CUSTOMER_VERIFY_CONTENT");
        content = content.replace("[[name]]", savedCustomer.getFullName());
        String verifyURL = frontendBaseUrl + "/verify?code=" + savedCustomer.getVerificationCode();
        content = content.replace("[[link]]", verifyURL);

        mimeMessageHelper.setText(content);

        mailSender.send(mimeMessage);
    }

    @Transactional
    public String verifyCustomer(String verificationCode) {
        Optional<Customer> OptionalCustomer = customerRepository.findCustomerByVerificationCode(verificationCode);


        if (OptionalCustomer.isPresent()) {
            Customer customer = OptionalCustomer.get();
            if (!customer.isEnabled()) {// dirty checking will occur here under the transaction block.
                customer.setEnabled(true);
                customer.setVerificationCode("");
                return "Customer Verified Successfully, Now you can login to the website";
            }
        }
        throw new DuplicatedObjectException("Customer is already verified or Invalid verification code/link");

    }

    @Transactional
    public Map<String, String> setPasswordToken(String customerEmail) {
        Optional<Customer> customerOptional = customerRepository.findCustomerByEmail(customerEmail);
        if (customerOptional.isEmpty())
            throw new ObjectNotFoundException("No Customers with this email: " + customerEmail);

        Customer customer = customerOptional.get();

        String token = RandomString.make(30);
        customer.setResetPasswordToken(token);

        customerRepository.save(customer);
        return Map.of("email", customerEmail, "token", token);
    }

    @Async
    public void sendResetPasswordEmail(Map<String, String> emailAndTokenMap) throws MessagingException {

        Map<String, String> mailSettings = settingsRepository
                .findByCategoryOrCategory(SettingCategory.MAIL_SERVER, SettingCategory.MAIL_TEMPLATES)
                .stream().collect(Collectors.toMap(Setting::getKey, Setting::getValue));

        // set mailServer(host), port, username, password, turn on Auth and TLS.
        JavaMailSenderImpl mailSender = MailUtility.prepareMailSender(mailSettings);

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);


        mimeMessageHelper.setFrom(mailSettings.get("MAIL_FROM"));
        mimeMessageHelper.setTo(emailAndTokenMap.get("email"));
        mimeMessageHelper.setSubject(mailSettings.get("CUSTOMER_RESET_PASSWORD_SUBJECT"));

        String content = mailSettings.get("CUSTOMER_RESET_PASSWORD_CONTENT");
        String verifyURL = frontendBaseUrl + "/reset-password?token=" + emailAndTokenMap.get("token");
        content = content.replace("[[link]]", verifyURL);

        mimeMessageHelper.setText(content);

        mailSender.send(mimeMessage);
    }


    @Transactional
    public void resetPassword(ResetPasswordRequest resetPasswordRequest) {
        Optional<Customer> customerOptional = customerRepository.findCustomerByResetPasswordToken(resetPasswordRequest.resetPasswordToken());
        if (customerOptional.isEmpty())
            throw new ObjectNotFoundException("Reset password link is corrupted");

        Customer customer = customerOptional.get();
        customer.setPassword(passwordEncoder.encode(resetPasswordRequest.password()));

        customer.setResetPasswordToken("");

        customerRepository.save(customer);
    }


    @Transactional(readOnly = true)
    public CustomerDTO getCustomerDetails(String customerEmail) {
        Optional<Customer> customerOptional = customerRepository.findCustomerByEmail(customerEmail);
        if (customerOptional.isEmpty())
            throw new ObjectNotFoundException("No Customers with this email: " + customerEmail);

        Customer customer = customerOptional.get();
        return customerMapper.toDTO(customer);
    }
}
