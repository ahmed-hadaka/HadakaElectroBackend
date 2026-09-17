package com.hadaka_electro.internal.category;

import java.io.IOException;
import java.util.List;

import com.hadaka_electro.internal.category.dto.CategoryListDTO;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import com.hadaka_electro.internal.exporter.AbstractExporter;

import jakarta.servlet.http.HttpServletResponse;

public class CategoryCsvExporter extends AbstractExporter {

	public void export(List<CategoryListDTO> categories, HttpServletResponse response) throws IOException {
		setupResponse("text/csv", "categories", ".csv", response);

		String[] csvHeaders = { "Category ID", "Name", "Alias", "Enabled" };
		String[] fieldMapping = { "id", "name", "alias", "enabled" };

		ICsvBeanWriter beanWriter = new CsvBeanWriter(response.getWriter(), CsvPreference.STANDARD_PREFERENCE);

		beanWriter.writeHeader(csvHeaders);

		for (CategoryListDTO category : categories) {
			beanWriter.write(category, fieldMapping);
		}

		beanWriter.close();
	}
}
