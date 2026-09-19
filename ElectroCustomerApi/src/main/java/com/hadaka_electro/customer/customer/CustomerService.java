package com.hadaka_electro.customer.customer;

import com.hadaka_electro.common.entities.Customer;
import com.hadaka_electro.common.entities.sitting.Country;
import com.hadaka_electro.common.entities.sitting.Sitting;
import com.hadaka_electro.common.entities.sitting.SittingCategory;
import com.hadaka_electro.customer.Utility;
import com.hadaka_electro.customer.sitting.repository.CountryRepository;
import com.hadaka_electro.customer.sitting.repository.SittingsRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
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
    private final SittingsRepository sittingsRepository;
    private final CustomerMapper customerMapper;

    @Value("${app.frontend.baseUrl}")
    private String frontendBaseUrl;

    public CustomerService(CustomerRepository customerRepository, CountryRepository countryRepository, PasswordEncoder passwordEncoder, SittingsRepository sittingsRepository, CustomerMapper customerMapper) {
        this.customerRepository = customerRepository;
        this.countryRepository = countryRepository;
        this.passwordEncoder = passwordEncoder;
        this.sittingsRepository = sittingsRepository;
        this.customerMapper = customerMapper;
    }

    @Transactional(readOnly = true)
    public List<Country> getAllCountries() {
        return countryRepository.findAllByNameAsc();
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
    public void sendVerificationEmail(Customer savedCustomer) throws MessagingException {
        Map<String, String> mailSittings = sittingsRepository
                .findByCategoryOrCategory(SittingCategory.MAIL_SERVER, SittingCategory.MAIL_TEMPLATES)
                .stream().collect(Collectors.toMap(Sitting::getKey, Sitting::getValue));

        // set mailServer(host), port, username, password, turn on Auth and TLS.
        JavaMailSenderImpl mailSender = Utility.prepareMailSender(mailSittings);

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);


        mimeMessageHelper.setFrom(mailSittings.get("MAIL_FROM"));
        mimeMessageHelper.setTo(savedCustomer.getEmail());
        mimeMessageHelper.setSubject(mailSittings.get("CUSTOMER_VERIFY_SUBJECT"));

        String content = mailSittings.get("CUSTOMER_VERIFY_CONTENT");
        content = content.replace("[[name]]", savedCustomer.getFullName());
        String verifyURL = frontendBaseUrl + "/customers/verify?code=" + savedCustomer.getVerificationCode();
        content = content.replace("[[link]]", verifyURL);

        mimeMessageHelper.setText(content);

        mailSender.send(mimeMessage);
    }

    @Transactional
    public String verifyCustomer(String verificationCode) {
        Optional<Customer> OptionalCustomer = customerRepository.findByVerificationCode(verificationCode);


        if (OptionalCustomer.isPresent()) {
            Customer customer = OptionalCustomer.get();
            if (!customer.isEnabled()) {// dirty checking will occur here under the transaction block.
                customer.setEnabled(true);
                customer.setVerificationCode("");
                return "Customer Verified Successfully, Now you can login to the website";
            }
        }
        return "Customer is already verified or Invalid verification code/link";

    }
}
