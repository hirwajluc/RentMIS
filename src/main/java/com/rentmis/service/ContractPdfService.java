package com.rentmis.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.AreaBreakType;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
import com.rentmis.model.entity.Contract;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Map;

@Slf4j
@Service
public class ContractPdfService {

    @Value("${app.base-url:http://86.48.7.218:5050}")
    private String baseUrl;

    private static final DeviceRgb DARK_BLUE  = new DeviceRgb(13, 43, 85);
    private static final DeviceRgb MID_BLUE   = new DeviceRgb(18, 47, 94);
    private static final DeviceRgb ACCENT     = new DeviceRgb(200, 168, 75);
    private static final DeviceRgb LIGHT_GREY = new DeviceRgb(245, 246, 250);
    private static final DeviceRgb GREEN      = new DeviceRgb(42, 157, 92);
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DT_FMT   = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public byte[] generate(Contract c) {
        boolean fullySignedParam = c.getLandlordSignedAt() != null && c.getTenantSignedAt() != null;
        return generate(c, fullySignedParam);
    }

    public byte[] generate(Contract c, boolean finalized) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(bos);
            PdfDocument pdf  = new PdfDocument(writer);

            // Register watermark handler BEFORE document is built
            PdfFont wFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
            if (!finalized) {
                pdf.addEventHandler(PdfDocumentEvent.END_PAGE, new WatermarkHandler("DRAFT", wFont));
            }

            Document doc = new Document(pdf, PageSize.A4);
            doc.setMargins(40, 50, 40, 50);

            PdfFont bold    = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
            PdfFont regular = PdfFontFactory.createFont(StandardFonts.HELVETICA);
            PdfFont mono    = PdfFontFactory.createFont(StandardFonts.COURIER);

            // ── Header: logo left + title right ─────────────────────────────
            Table header = new Table(UnitValue.createPercentArray(new float[]{2.2f, 5f}))
                    .useAllAvailableWidth()
                    .setBackgroundColor(DARK_BLUE);

            // Logo cell — draw icon + wordmark via PdfFormXObject
            float iconSz = 36f;
            PdfFormXObject logoXObj = new PdfFormXObject(new Rectangle(0, 0, iconSz, iconSz));
            PdfCanvas lc = new PdfCanvas(logoXObj, pdf);
            drawLogoIcon(lc, 0, 0, iconSz);
            lc.release();

            Cell logoCell = new Cell()
                    .add(new Image(logoXObj).setAutoScale(false).setHeight(iconSz).setWidth(iconSz))
                    .add(new Paragraph("RentMIS")
                            .setFont(bold).setFontSize(13).setFontColor(ColorConstants.WHITE)
                            .setMarginTop(4).setMarginBottom(0))
                    .add(new Paragraph("RENT MANAGEMENT SYSTEM")
                            .setFont(regular).setFontSize(6).setFontColor(ACCENT)
                            .setCharacterSpacing(1f).setMarginTop(0))
                    .setBorder(Border.NO_BORDER)
                    .setPadding(10)
                    .setVerticalAlignment(VerticalAlignment.MIDDLE);

            // Title cell
            Cell titleCell = new Cell()
                    .add(new Paragraph("COMMERCIAL RENT AGREEMENT")
                            .setFont(bold).setFontSize(15).setFontColor(ColorConstants.WHITE)
                            .setTextAlignment(TextAlignment.CENTER).setMarginBottom(2))
                    .add(new Paragraph("BLOCKCHAIN-SIGNED DIGITAL CONTRACT")
                            .setFont(regular).setFontSize(9).setFontColor(ACCENT)
                            .setTextAlignment(TextAlignment.CENTER))
                    .setBorder(Border.NO_BORDER)
                    .setPaddingTop(14).setPaddingBottom(14).setPaddingRight(14)
                    .setVerticalAlignment(VerticalAlignment.MIDDLE);

            header.addCell(logoCell);
            header.addCell(titleCell);
            doc.add(header);

            // ── Agreement meta box ───────────────────────────────────────────
            doc.add(new Paragraph("").setMarginTop(8));
            Table meta = new Table(UnitValue.createPercentArray(new float[]{1, 1}))
                    .useAllAvailableWidth()
                    .setBackgroundColor(LIGHT_GREY);
            meta.addCell(metaCell(bold, regular, "Agreement ID", c.getContractNumber()));
            meta.addCell(metaCell(bold, regular, "Date of Agreement",
                    c.getCreatedAt() != null ? c.getCreatedAt().format(DATE_FMT) : "—"));
            meta.addCell(metaCell(bold, regular, "Smart Contract Ref",
                    c.getBlockchainTxHash() != null ? c.getBlockchainTxHash() : "Pending signing"));
            meta.addCell(metaCell(bold, regular, "Network",
                    c.getBlockchainNetwork() != null ? c.getBlockchainNetwork() : "RentMIS-CryptoRef-v1"));
            doc.add(meta);

            // ── Section 1: Parties ───────────────────────────────────────────
            doc.add(sectionTitle(bold, "1. PARTIES"));

            Table parties = new Table(UnitValue.createPercentArray(new float[]{1, 1}))
                    .useAllAvailableWidth();

            // Landlord
            Cell lCell = new Cell()
                    .add(new Paragraph("LANDLORD").setFont(bold).setFontSize(10).setFontColor(DARK_BLUE))
                    .add(labelValue(regular, bold, "Name",
                            c.getLandlord().getFirstName() + " " + c.getLandlord().getLastName()))
                    .add(labelValue(regular, bold, "Email", c.getLandlord().getEmail()))
                    .add(labelValue(regular, bold, "Phone",
                            nvl(c.getLandlord().getPhone())))
                    .add(labelValue(regular, bold, "ID/Reg No",
                            nvl(c.getLandlord().getNationalId())))
                    .setBorder(new SolidBorder(ACCENT, 0.5f)).setPadding(10);
            parties.addCell(lCell);

            // Tenant
            Cell tCell = new Cell()
                    .add(new Paragraph("TENANT").setFont(bold).setFontSize(10).setFontColor(DARK_BLUE))
                    .add(labelValue(regular, bold, "Name",
                            c.getTenant().getFirstName() + " " + c.getTenant().getLastName()))
                    .add(labelValue(regular, bold, "Email", c.getTenant().getEmail()))
                    .add(labelValue(regular, bold, "Phone",
                            nvl(c.getTenant().getPhone())))
                    .add(labelValue(regular, bold, "ID/Reg No",
                            nvl(c.getTenant().getNationalId())))
                    .setBorder(new SolidBorder(ACCENT, 0.5f)).setPadding(10);
            parties.addCell(tCell);
            doc.add(parties);

            // ── Section 2: Property Details ──────────────────────────────────
            doc.add(sectionTitle(bold, "2. PROPERTY DETAILS"));
            Table prop = twoColTable();
            String propertyName = c.getUnit().getProperty() != null
                    ? c.getUnit().getProperty().getName() : "—";
            String address = c.getUnit().getProperty() != null
                    ? buildAddress(c) : "—";
            prop.addCell(kvCell(regular, bold, "Property", propertyName));
            prop.addCell(kvCell(regular, bold, "Unit Number", c.getUnit().getUnitNumber()));
            prop.addCell(kvCell(regular, bold, "Location", address));
            prop.addCell(kvCell(regular, bold, "Unit Type",
                    nvl(c.getUnit().getUnitType())));
            prop.addCell(kvCell(regular, bold, "Use", "Commercial"));
            prop.addCell(kvCell(regular, bold, "Area (m²)",
                    c.getUnit().getAreaSqm() != null ? c.getUnit().getAreaSqm().toPlainString() : "—"));
            doc.add(prop);

            // ── Section 3: Lease Term ────────────────────────────────────────
            doc.add(sectionTitle(bold, "3. LEASE TERM"));
            Table term = twoColTable();
            term.addCell(kvCell(regular, bold, "Start Date", c.getStartDate().format(DATE_FMT)));
            term.addCell(kvCell(regular, bold, "End Date",   c.getEndDate().format(DATE_FMT)));
            long months = ChronoUnit.MONTHS.between(c.getStartDate(), c.getEndDate());
            term.addCell(kvCell(regular, bold, "Duration", months + " months (" +
                    Math.round(months / 12.0 * 10) / 10.0 + " years)"));
            term.addCell(kvCell(regular, bold, "Status", c.getStatus().name()));
            doc.add(term);

            // ── Section 4: Rent Payment ──────────────────────────────────────
            doc.add(sectionTitle(bold, "4. RENT PAYMENT"));
            Table rent = twoColTable();
            rent.addCell(kvCell(regular, bold, "Monthly Rent",
                    "RWF " + c.getMonthlyRent().toPlainString()));
            rent.addCell(kvCell(regular, bold, "Payment Mode", "Bank / Mobile Money (GLSPay)"));
            rent.addCell(new Cell(1, 2)
                    .add(new Paragraph("Automation: Payments may be executed via smart contract triggers.")
                            .setFont(regular).setFontSize(9).setFontColor(ColorConstants.DARK_GRAY))
                    .setBorder(Border.NO_BORDER).setPaddingLeft(6).setPaddingBottom(4));
            doc.add(rent);

            // ── Section 5: Security Deposit ──────────────────────────────────
            doc.add(sectionTitle(bold, "5. SECURITY DEPOSIT"));
            Table dep = twoColTable();
            dep.addCell(kvCell(regular, bold, "Deposit Amount",
                    c.getDepositAmount() != null
                            ? "RWF " + c.getDepositAmount().toPlainString() : "None"));
            dep.addCell(kvCell(regular, bold, "Held In", "Landlord Custody"));
            dep.addCell(new Cell(1, 2)
                    .add(new Paragraph("Refund Conditions: Based on contract rules and inspection record.")
                            .setFont(regular).setFontSize(9).setFontColor(ColorConstants.DARK_GRAY))
                    .setBorder(Border.NO_BORDER).setPaddingLeft(6).setPaddingBottom(4));
            doc.add(dep);

            // ── Sections 6 & 7: Obligations ─────────────────────────────────
            doc.add(sectionTitle(bold, "6. OBLIGATIONS OF THE LANDLORD"));
            doc.add(obligationList(regular, new String[]{
                "Provide usable premises in good condition",
                "Ensure peaceful possession by the tenant",
                "Handle all major structural repairs",
                "Register the agreement hash on the RentMIS blockchain layer",
                "Not tamper with recorded contract data"
            }));

            doc.add(sectionTitle(bold, "7. OBLIGATIONS OF THE TENANT"));
            doc.add(obligationList(regular, new String[]{
                "Pay rent on time as per the agreed schedule",
                "Use the premises only for the agreed commercial purpose",
                "Maintain minor repairs and keep premises clean",
                "Not sublet without written consent from the landlord",
                "Digitally sign and validate this agreement on-chain"
            }));

            // ── Sections 8-11 ────────────────────────────────────────────────
            doc.add(sectionTitle(bold, "8. MAINTENANCE & UTILITIES"));
            doc.add(new Paragraph("Utilities responsibility is as agreed between both parties. " +
                    "Maintenance splits are clearly defined and optionally logged on-chain.")
                    .setFont(regular).setFontSize(10).setMarginLeft(10).setMarginBottom(6));

            doc.add(sectionTitle(bold, "9. TERMINATION"));
            doc.add(obligationList(regular, new String[]{
                "Notice Period: 30 days written notice required",
                "Early termination conditions are encoded in the system",
                "Termination events are recorded on the blockchain layer"
            }));
            if (c.getTerminationReason() != null) {
                doc.add(new Paragraph("Termination Reason: " + c.getTerminationReason())
                        .setFont(bold).setFontSize(10).setFontColor(ColorConstants.RED).setMarginLeft(10));
            }

            doc.add(sectionTitle(bold, "10. DISPUTE RESOLUTION"));
            doc.add(obligationList(regular, new String[]{
                "Off-chain negotiation between the parties",
                "Arbitration or Courts of Rwanda",
                "Note: The blockchain record serves as tamper-proof evidence, not automatic legal " +
                        "enforcement unless recognized by law"
            }));

            doc.add(sectionTitle(bold, "11. GOVERNING LAW"));
            doc.add(new Paragraph("This agreement is governed by the laws of Rwanda. " +
                    "The RentMIS blockchain layer serves as a recording and verification mechanism.")
                    .setFont(regular).setFontSize(10).setMarginLeft(10).setMarginBottom(6));

            // ── Terms & Conditions ────────────────────────────────────────────
            if (c.getTermsConditions() != null && !c.getTermsConditions().isBlank()) {
                doc.add(sectionTitle(bold, "TERMS & CONDITIONS"));
                doc.add(new Paragraph(c.getTermsConditions())
                        .setFont(regular).setFontSize(9).setMarginLeft(10).setMarginBottom(6));
            }
            if (c.getSpecialClauses() != null && !c.getSpecialClauses().isBlank()) {
                doc.add(sectionTitle(bold, "SPECIAL CLAUSES"));
                doc.add(new Paragraph(c.getSpecialClauses())
                        .setFont(regular).setFontSize(9).setMarginLeft(10).setMarginBottom(6));
            }

            // ── Section 12: Digital Signatures — force new page ──────────────
            doc.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
            doc.add(sectionTitle(bold, "12. DIGITAL SIGNATURE & BLOCKCHAIN RECORD"));
            doc.add(new Paragraph(
                    "By signing below, both parties agree that this agreement is digitally executed, " +
                    "a SHA-256 hash of this document is stored on the RentMIS blockchain layer, " +
                    "and HMAC signatures represent binding consent.")
                    .setFont(regular).setFontSize(10).setMarginLeft(10).setMarginBottom(8));

            // Signature block header
            Table sigHeader = new Table(UnitValue.createPercentArray(new float[]{1}))
                    .useAllAvailableWidth()
                    .setBackgroundColor(MID_BLUE);
            sigHeader.addCell(new Cell()
                    .add(new Paragraph("SIGNATURE RECORD")
                            .setFont(bold).setFontSize(11).setFontColor(ColorConstants.WHITE)
                            .setTextAlignment(TextAlignment.CENTER))
                    .setBorder(Border.NO_BORDER).setPadding(8));
            doc.add(sigHeader);

            // Signature details — side-by-side, hash split into 2×32-char lines
            Table sigs = new Table(UnitValue.createPercentArray(new float[]{1, 1}))
                    .useAllAvailableWidth();

            // Landlord signature
            Cell lSig = new Cell()
                    .add(new Paragraph("LANDLORD SIGNATURE")
                            .setFont(bold).setFontSize(10).setFontColor(DARK_BLUE))
                    .add(labelValue(regular, bold, "Name",
                            c.getLandlord().getFirstName() + " " + c.getLandlord().getLastName()))
                    .add(labelValue(regular, bold, "Signed At",
                            c.getLandlordSignedAt() != null
                                    ? c.getLandlordSignedAt().format(DT_FMT) : "Not yet signed"))
                    .add(labelValue(regular, bold, "IP", nvl(c.getLandlordSignatureIp())))
                    .setBorder(new SolidBorder(c.getLandlordSignedAt() != null ? GREEN : ACCENT, 1f))
                    .setPaddingTop(10).setPaddingBottom(10).setPaddingLeft(8).setPaddingRight(8);
            if (c.getLandlordSignature() != null) {
                lSig.add(new Paragraph("HMAC-SHA256 Signature:")
                        .setFont(bold).setFontSize(7).setFontColor(ColorConstants.DARK_GRAY).setMarginTop(5));
                lSig.add(new Paragraph(splitHex(c.getLandlordSignature()))
                        .setFont(mono).setFontSize(6.5f).setFontColor(MID_BLUE)
                        .setWordSpacing(0).setCharacterSpacing(0));
            }
            sigs.addCell(lSig);

            // Tenant signature
            Cell tSig = new Cell()
                    .add(new Paragraph("TENANT SIGNATURE")
                            .setFont(bold).setFontSize(10).setFontColor(DARK_BLUE))
                    .add(labelValue(regular, bold, "Name",
                            c.getTenant().getFirstName() + " " + c.getTenant().getLastName()))
                    .add(labelValue(regular, bold, "Signed At",
                            c.getTenantSignedAt() != null
                                    ? c.getTenantSignedAt().format(DT_FMT) : "Not yet signed"))
                    .add(labelValue(regular, bold, "IP", nvl(c.getTenantSignatureIp())))
                    .setBorder(new SolidBorder(c.getTenantSignedAt() != null ? GREEN : ACCENT, 1f))
                    .setPaddingTop(10).setPaddingBottom(10).setPaddingLeft(8).setPaddingRight(8);
            if (c.getTenantSignature() != null) {
                tSig.add(new Paragraph("HMAC-SHA256 Signature:")
                        .setFont(bold).setFontSize(7).setFontColor(ColorConstants.DARK_GRAY).setMarginTop(5));
                tSig.add(new Paragraph(splitHex(c.getTenantSignature()))
                        .setFont(mono).setFontSize(6.5f).setFontColor(MID_BLUE)
                        .setWordSpacing(0).setCharacterSpacing(0));
            }
            sigs.addCell(tSig);
            doc.add(sigs);

            // Contract hash & blockchain ref — with QR code
            if (c.getContractHash() != null) {
                doc.add(new Paragraph("").setMarginTop(8));

                String verifyUrl = baseUrl + "/html/verify.html?contract=" + c.getContractNumber();

                // Two-column: text left (85%), QR right (15%)
                Table hashBox = new Table(UnitValue.createPercentArray(new float[]{85f, 15f}))
                        .useAllAvailableWidth()
                        .setBackgroundColor(LIGHT_GREY);

                // Left cell: hash + blockchain ref + verify text
                Cell textCell = new Cell()
                        .add(new Paragraph("Contract Integrity Hash (SHA-256)")
                                .setFont(bold).setFontSize(9).setFontColor(DARK_BLUE))
                        .add(new Paragraph(c.getContractHash())
                                .setFont(mono).setFontSize(8).setFontColor(MID_BLUE))
                        .add(c.getBlockchainTxHash() != null
                                ? new Paragraph("Blockchain Reference: " + c.getBlockchainTxHash())
                                        .setFont(mono).setFontSize(8).setFontColor(GREEN)
                                : new Paragraph("Blockchain reference will be recorded once both parties sign.")
                                        .setFont(regular).setFontSize(8).setFontColor(ColorConstants.DARK_GRAY))
                        .add(new Paragraph("Scan QR or visit: " + verifyUrl)
                                .setFont(regular).setFontSize(8).setFontColor(ColorConstants.DARK_GRAY)
                                .setMarginTop(4))
                        .setBorder(new SolidBorder(ACCENT, 0.5f))
                        .setBorderRight(Border.NO_BORDER)
                        .setPadding(8);
                hashBox.addCell(textCell);

                // Right cell: QR code image
                Cell qrCell = new Cell()
                        .setBorder(new SolidBorder(ACCENT, 0.5f))
                        .setBorderLeft(Border.NO_BORDER)
                        .setPadding(6)
                        .setVerticalAlignment(VerticalAlignment.MIDDLE)
                        .setHorizontalAlignment(HorizontalAlignment.CENTER);
                try {
                    byte[] qrBytes = generateQrCode(verifyUrl, 120);
                    Image qrImg = new Image(ImageDataFactory.create(qrBytes))
                            .setWidth(70).setHeight(70)
                            .setHorizontalAlignment(HorizontalAlignment.CENTER);
                    qrCell.add(qrImg);
                    qrCell.add(new Paragraph("Verify")
                            .setFont(regular).setFontSize(7).setFontColor(ColorConstants.DARK_GRAY)
                            .setTextAlignment(TextAlignment.CENTER).setMarginTop(2));
                } catch (Exception qrEx) {
                    log.warn("QR code generation failed: {}", qrEx.getMessage());
                    qrCell.add(new Paragraph("QR\nN/A").setFont(regular).setFontSize(7)
                            .setTextAlignment(TextAlignment.CENTER));
                }
                hashBox.addCell(qrCell);

                doc.add(hashBox);
            }

            // Final executed stamp
            if (finalized) {
                doc.add(new Paragraph("").setMarginTop(8));
                Table stamp = new Table(UnitValue.createPercentArray(new float[]{1}))
                        .useAllAvailableWidth()
                        .setBackgroundColor(GREEN);
                stamp.addCell(new Cell()
                        .add(new Paragraph("✔  AGREEMENT FULLY EXECUTED — LEGALLY BINDING")
                                .setFont(bold).setFontSize(11).setFontColor(ColorConstants.WHITE)
                                .setTextAlignment(TextAlignment.CENTER))
                        .add(new Paragraph("Both parties have digitally signed. This contract is final and immutable.")
                                .setFont(regular).setFontSize(9).setFontColor(ColorConstants.WHITE)
                                .setTextAlignment(TextAlignment.CENTER))
                        .setBorder(Border.NO_BORDER).setPadding(10));
                doc.add(stamp);
            }

            // Footer
            doc.add(new Paragraph("").setMarginTop(16));
            doc.add(new Paragraph(
                    (finalized ? "FINAL EXECUTED COPY  |  " : "DRAFT — NOT YET FULLY SIGNED  |  ") +
                    "RentMIS — Rent Management Information System  |  " +
                    "Generated: " + java.time.LocalDateTime.now().format(DT_FMT))
                    .setFont(regular).setFontSize(8)
                    .setFontColor(finalized ? GREEN : ColorConstants.GRAY)
                    .setTextAlignment(TextAlignment.CENTER));

            doc.close();
            return bos.toByteArray();

        } catch (Exception e) {
            log.error("PDF generation failed for contract {}: {}", e.getMessage(), e);
            throw new RuntimeException("PDF generation failed: " + e.getMessage(), e);
        }
    }

    // ── Helpers ────────────────────────────────────────────────────────────────

    private Paragraph sectionTitle(PdfFont bold, String text) {
        return new Paragraph(text)
                .setFont(bold).setFontSize(11).setFontColor(DARK_BLUE)
                .setMarginTop(12).setMarginBottom(4)
                .setBorderBottom(new SolidBorder(ACCENT, 0.8f))
                .setPaddingBottom(2);
    }

    private Cell metaCell(PdfFont bold, PdfFont regular, String label, String value) {
        return new Cell()
                .add(new Paragraph(label).setFont(bold).setFontSize(8).setFontColor(ColorConstants.DARK_GRAY))
                .add(new Paragraph(value).setFont(regular).setFontSize(10))
                .setBorder(Border.NO_BORDER).setPadding(6);
    }

    private Table twoColTable() {
        return new Table(UnitValue.createPercentArray(new float[]{1, 1}))
                .useAllAvailableWidth();
    }

    private Cell kvCell(PdfFont regular, PdfFont bold, String key, String value) {
        return new Cell()
                .add(new Paragraph(key + ":").setFont(bold).setFontSize(9)
                        .setFontColor(ColorConstants.DARK_GRAY).setMarginBottom(0))
                .add(new Paragraph(value).setFont(regular).setFontSize(10).setMarginTop(0))
                .setBorder(Border.NO_BORDER).setPadding(5);
    }

    private Paragraph labelValue(PdfFont regular, PdfFont bold, String label, String value) {
        Text l = new Text(label + ": ").setFont(bold).setFontSize(9).setFontColor(ColorConstants.DARK_GRAY);
        Text v = new Text(value).setFont(regular).setFontSize(9);
        return new Paragraph().add(l).add(v).setMarginBottom(2);
    }

    private Paragraph obligationList(PdfFont regular, String[] items) {
        StringBuilder sb = new StringBuilder();
        for (String item : items) sb.append("  •  ").append(item).append("\n");
        return new Paragraph(sb.toString().trim())
                .setFont(regular).setFontSize(10).setMarginLeft(10).setMarginBottom(6);
    }

    private String buildAddress(Contract c) {
        var p = c.getUnit().getProperty();
        if (p == null) return "—";
        StringBuilder sb = new StringBuilder();
        if (p.getAddress() != null)  sb.append(p.getAddress());
        if (p.getSector() != null)   { if (sb.length() > 0) sb.append(", "); sb.append(p.getSector()); }
        if (p.getDistrict() != null) { if (sb.length() > 0) sb.append(", "); sb.append(p.getDistrict()); }
        if (p.getCity() != null)     { if (sb.length() > 0) sb.append(", "); sb.append(p.getCity()); }
        return sb.length() > 0 ? sb.toString() : "—";
    }

    private String nvl(String s) { return s != null && !s.isBlank() ? s : "—"; }

    /** Splits a hex string into two 32-char lines so it fits inside a half-page cell. */
    private String splitHex(String hex) {
        if (hex == null || hex.length() <= 32) return hex;
        return hex.substring(0, 32) + "\n" + hex.substring(32);
    }

    /**
     * Generates a QR code PNG as a byte array using ZXing.
     */
    private byte[] generateQrCode(String content, int size) throws Exception {
        QRCodeWriter writer = new QRCodeWriter();
        Map<EncodeHintType, Object> hints = new java.util.EnumMap<>(EncodeHintType.class);
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
        hints.put(EncodeHintType.MARGIN, 1);
        BitMatrix matrix = writer.encode(content, BarcodeFormat.QR_CODE, size, size, hints);
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(matrix, "PNG", out);
        return out.toByteArray();
    }

    /**
     * Draws the RentMIS icon (house + door) onto a PdfCanvas.
     * Matches the SVG logo used in the web UI.
     * Origin (x, y) is the bottom-left corner; s is the icon size.
     */
    private void drawLogoIcon(PdfCanvas c, float x, float y, float s) {
        // Background: dark-blue rounded square
        c.saveState()
         .setFillColor(DARK_BLUE)
         .roundRectangle(x, y, s, s, s * 0.18f)
         .fill()
         .restoreState();

        // Roof (triangle): peak at top-centre, feet at 33% height
        float peakX  = x + s * 0.50f;  float peakY  = y + s * 0.86f;
        float leftX  = x + s * 0.14f;  float leftY  = y + s * 0.60f;
        float rightX = x + s * 0.86f;  float rightY = y + s * 0.60f;
        c.saveState()
         .setStrokeColor(ColorConstants.WHITE)
         .setLineWidth(s * 0.045f)
         .moveTo(peakX, peakY).lineTo(leftX, leftY).lineTo(rightX, rightY)
         .closePathStroke()
         .restoreState();

        // Body (rectangle below roof)
        float bx = x + s * 0.19f;  float by = y + s * 0.22f;
        float bw = s * 0.62f;      float bh = s * 0.40f;
        c.saveState()
         .setStrokeColor(ColorConstants.WHITE)
         .setLineWidth(s * 0.045f)
         .rectangle(bx, by, bw, bh)
         .stroke()
         .restoreState();

        // Door (green filled rect centred in body)
        float dw = bw * 0.32f;  float dh = bh * 0.55f;
        float dx = bx + (bw - dw) / 2f;  float dy = by;
        c.saveState()
         .setFillColor(GREEN)
         .rectangle(dx, dy, dw, dh)
         .fill()
         .restoreState();
    }

    // ── Watermark handler (fires on each page) ─────────────────────────────
    private static class WatermarkHandler implements IEventHandler {
        private final String text;
        private final PdfFont font;

        WatermarkHandler(String text, PdfFont font) {
            this.text = text;
            this.font = font;
        }

        @Override
        public void handleEvent(Event event) {
            PdfDocumentEvent e = (PdfDocumentEvent) event;
            PdfDocument pdfDoc = e.getDocument();
            PdfPage page = e.getPage();
            Rectangle rect = page.getPageSize();

            try {
                PdfCanvas pdfCanvas = new PdfCanvas(page.newContentStreamBefore(),
                        page.getResources(), pdfDoc);
                float angle = (float) Math.toRadians(45);
                float cos   = (float) Math.cos(angle);
                float sin   = (float) Math.sin(angle);
                float cx    = rect.getWidth()  / 2f;
                float cy    = rect.getHeight() / 2f;

                pdfCanvas.saveState();
                // Very light gray — barely visible, non-intrusive
                pdfCanvas.setFillColor(new DeviceRgb(232, 232, 232));
                pdfCanvas.beginText()
                        .setFontAndSize(font, 55)
                        .setTextMatrix(cos, sin, -sin, cos, cx - 55, cy - 14)
                        .showText(text)
                        .endText();
                pdfCanvas.restoreState();
                pdfCanvas.release();
            } catch (Exception ex) {
                // watermark is cosmetic — don't fail PDF generation
            }
        }
    }
}
