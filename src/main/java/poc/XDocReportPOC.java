package poc;

import fr.opensagres.xdocreport.converter.ConverterTypeTo;
import fr.opensagres.xdocreport.converter.ConverterTypeVia;
import fr.opensagres.xdocreport.converter.Options;
import fr.opensagres.xdocreport.core.XDocReportException;
import fr.opensagres.xdocreport.document.IXDocReport;
import fr.opensagres.xdocreport.document.registry.XDocReportRegistry;
import fr.opensagres.xdocreport.template.IContext;
import fr.opensagres.xdocreport.template.TemplateEngineKind;
import fr.opensagres.xdocreport.template.formatter.FieldsMetadata;

import java.io.*;

public class XDocReportPOC {

    public static void main(String[] args) throws XDocReportException, IOException {

        // 1) Load Docx file by filling Velocity template engine and cache
        // it to the registry
        InputStream in = XDocReportPOC.class.getResourceAsStream("/test.docx");

        IXDocReport report = XDocReportRegistry.getRegistry().loadReport(in, TemplateEngineKind.Freemarker);

        // 2) Create fields metadata to manage lazy loop (#forech velocity)
        // for table row.
        // 1) Create FieldsMetadata by setting Velocity as template engine
        FieldsMetadata fieldsMetadata = report.createFieldsMetadata();
        // 2) Load fields metadata from Java Class
        fieldsMetadata.load("client", Client.class);

        // 3) Create context Java model
        IContext context = report.createContext();
        Client client = new Client("Jan", "Kowalski");
        context.put("client", client);

        // 4) Generate report by merging Java model with the Docx
//        DOCX
        OutputStream outDocx = new FileOutputStream(new File("project_out.docx"));
        report.process(context, outDocx);

//        PDF
        OutputStream outPdf = new FileOutputStream(new File("project_out.pdf"));
        Options options = Options.getTo(ConverterTypeTo.PDF).via(ConverterTypeVia.XWPF);
        report.convert(context, options, outPdf);
    }

}
