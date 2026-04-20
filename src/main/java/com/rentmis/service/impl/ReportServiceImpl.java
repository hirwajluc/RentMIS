package com.rentmis.service.impl;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.AreaBreakType;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
import com.rentmis.dto.response.ReportData;
import com.rentmis.model.enums.ContractStatus;
import com.rentmis.repository.ContractRepository;
import com.rentmis.repository.InvoiceRepository;
import com.rentmis.repository.PaymentRepository;
import com.rentmis.repository.PropertyRepository;
import com.rentmis.repository.UnitRepository;
import com.rentmis.repository.UserRepository;
import com.rentmis.model.enums.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportServiceImpl {

    private final PaymentRepository  paymentRepository;
    private final ContractRepository contractRepository;
    private final UnitRepository     unitRepository;
    private final PropertyRepository propertyRepository;
    private final UserRepository     userRepository;
    private final InvoiceRepository  invoiceRepository;

    // ── Colours & fonts ───────────────────────────────────────────────────────
    private static final DeviceRgb BRAND_DARK   = new DeviceRgb(13,  43,  85);   // #0D2B55
    private static final DeviceRgb BRAND_BLUE   = new DeviceRgb(105, 108, 255);  // #696cff
    private static final DeviceRgb BRAND_GREEN  = new DeviceRgb(113, 221, 55);   // #71dd37
    private static final DeviceRgb BRAND_YELLOW = new DeviceRgb(255, 171, 0);    // #ffab00
    private static final DeviceRgb BRAND_RED    = new DeviceRgb(255, 62,  29);   // #ff3e1d
    private static final DeviceRgb LIGHT_GREY   = new DeviceRgb(245, 245, 250);
    private static final DeviceRgb MID_GREY     = new DeviceRgb(140, 153, 179);
    private static final DeviceRgb TABLE_HEADER = new DeviceRgb(230, 232, 255);

    // ── Build report data ─────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public ReportData buildReport(Long landlordId) {
        boolean isAdmin = landlordId == null;
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime yearStart = now.withDayOfYear(1).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime trendStart = now.minusMonths(12);

        // ── Financial ─────────────────────────────────────────────────────────
        BigDecimal totalRevenue = nvl(isAdmin
                ? paymentRepository.totalRevenue()
                : paymentRepository.totalRevenueByLandlord(landlordId));
        BigDecimal annualRevenue = nvl(isAdmin
                ? paymentRepository.sumCompletedPayments(yearStart, now)
                : paymentRepository.sumCompletedPaymentsByLandlord(yearStart, now, landlordId));
        BigDecimal monthlyRevenue = nvl(isAdmin
                ? paymentRepository.sumCompletedByMonthYear(now.getMonthValue(), now.getYear())
                : paymentRepository.sumCompletedByMonthYearAndLandlord(now.getMonthValue(), now.getYear(), landlordId));
        Long completed = nvlL(isAdmin
                ? paymentRepository.countCompletedPayments()
                : paymentRepository.countCompletedPaymentsByLandlord(landlordId));
        Long pending = nvlL(isAdmin
                ? paymentRepository.countPendingPayments()
                : paymentRepository.countPendingPaymentsByLandlord(landlordId));
        Long failed = nvlL(isAdmin
                ? paymentRepository.countFailedPayments()
                : paymentRepository.countFailedPaymentsByLandlord(landlordId));
        BigDecimal avgPayment = nvl(isAdmin
                ? paymentRepository.avgCompletedAmount()
                : paymentRepository.avgCompletedAmountByLandlord(landlordId));

        // ── Occupancy ─────────────────────────────────────────────────────────
        long totalUnits = isAdmin
                ? unitRepository.countActiveUnits()
                : unitRepository.countByLandlordId(landlordId);
        long occupied = isAdmin
                ? unitRepository.countOccupiedUnits()
                : unitRepository.countOccupiedByLandlordId(landlordId);
        long available = totalUnits - occupied;
        double occRate = totalUnits > 0 ? BigDecimal.valueOf((double) occupied / totalUnits * 100)
                .setScale(1, RoundingMode.HALF_UP).doubleValue() : 0.0;
        long totalProps = isAdmin
                ? propertyRepository.countActiveProperties()
                : propertyRepository.countByLandlordId(landlordId);

        // ── Contracts ─────────────────────────────────────────────────────────
        List<Object[]> contractCounts = isAdmin
                ? contractRepository.countByStatus()
                : contractRepository.countByStatusForLandlord(landlordId);
        Map<String, Long> cMap = contractCounts.stream()
                .collect(Collectors.toMap(r -> r[0].toString(), r -> ((Number) r[1]).longValue()));
        long activeC    = cMap.getOrDefault("ACTIVE", 0L);
        long expiredC   = cMap.getOrDefault("EXPIRED", 0L);
        long terminatedC = cMap.getOrDefault("TERMINATED", 0L);
        long pendingSigC = cMap.getOrDefault("PENDING_SIGNATURE", 0L);
        long totalC     = cMap.values().stream().mapToLong(Long::longValue).sum();

        // ── Tenants / Landlords ───────────────────────────────────────────────
        long totalTenants = isAdmin
                ? userRepository.countByRoleAndActive(Role.TENANT)
                : userRepository.countTenantsByLandlordId(landlordId);
        long totalLandlords = isAdmin ? userRepository.countByRoleAndActive(Role.LANDLORD) : 0L;

        // ── Invoices / EBM ────────────────────────────────────────────────────
        long totalInvoices = isAdmin
                ? invoiceRepository.count()
                : invoiceRepository.countByLandlordId(landlordId);
        long ebmSuccess = isAdmin
                ? invoiceRepository.countByEbmStatus("SUCCESS")
                : invoiceRepository.countByLandlordIdAndEbmStatus(landlordId, "SUCCESS");
        long ebmPending = isAdmin
                ? invoiceRepository.countByEbmStatus("PENDING")
                : invoiceRepository.countByLandlordIdAndEbmStatus(landlordId, "PENDING");
        long ebmFailed  = isAdmin
                ? invoiceRepository.countByEbmStatus("FAILED")
                : invoiceRepository.countByLandlordIdAndEbmStatus(landlordId, "FAILED");

        // ── Monthly trend with counts ─────────────────────────────────────────
        List<Object[]> trendRows = isAdmin
                ? paymentRepository.monthlyTrendWithCount(trendStart)
                : paymentRepository.monthlyTrendWithCountByLandlord(trendStart, landlordId);
        List<ReportData.MonthRow> trend = trendRows.stream().map(r -> {
            int mo = ((Number) r[0]).intValue(), yr = ((Number) r[1]).intValue();
            return ReportData.MonthRow.builder()
                    .label(Month.of(mo).getDisplayName(TextStyle.SHORT, Locale.ENGLISH) + " " + yr)
                    .revenue(r[2] != null ? new BigDecimal(r[2].toString()) : BigDecimal.ZERO)
                    .count(r[3] != null ? ((Number) r[3]).longValue() : 0L)
                    .build();
        }).collect(Collectors.toList());

        // ── Top payers ────────────────────────────────────────────────────────
        List<Object[]> payerRows = isAdmin
                ? paymentRepository.topPayers(PageRequest.of(0, 5))
                : paymentRepository.topPayersByLandlord(landlordId, PageRequest.of(0, 5));
        List<ReportData.TopPayer> topPayers = payerRows.stream().map(r ->
                ReportData.TopPayer.builder()
                        .tenantName(r[1] + " " + r[2])
                        .paymentCount(((Number) r[3]).longValue())
                        .totalPaid(r[4] != null ? new BigDecimal(r[4].toString()) : BigDecimal.ZERO)
                        .build()
        ).collect(Collectors.toList());

        // ── Payment methods ───────────────────────────────────────────────────
        List<Object[]> methRows = isAdmin
                ? paymentRepository.paymentMethodBreakdown()
                : paymentRepository.paymentMethodBreakdownByLandlord(landlordId);
        List<ReportData.MethodRow> methods = methRows.stream().map(r ->
                ReportData.MethodRow.builder()
                        .method(r[0] != null ? r[0].toString() : "UNKNOWN")
                        .count(((Number) r[1]).longValue())
                        .total(r[2] != null ? new BigDecimal(r[2].toString()) : BigDecimal.ZERO)
                        .build()
        ).collect(Collectors.toList());

        return ReportData.builder()
                .generatedAt(now.format(DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm")))
                .period("Last 12 months (up to " + now.format(DateTimeFormatter.ofPattern("dd MMM yyyy")) + ")")
                .totalRevenue(totalRevenue)
                .annualRevenue(annualRevenue)
                .monthlyRevenue(monthlyRevenue)
                .completedPayments(completed)
                .pendingPayments(pending)
                .failedPayments(failed)
                .avgPaymentAmount(avgPayment)
                .totalUnits(totalUnits)
                .occupiedUnits(occupied)
                .availableUnits(available)
                .occupancyRate(occRate)
                .totalProperties(totalProps)
                .activeContracts(activeC)
                .expiredContracts(expiredC)
                .terminatedContracts(terminatedC)
                .pendingSignatureContracts(pendingSigC)
                .totalContracts(totalC)
                .totalTenants(totalTenants)
                .totalLandlords(totalLandlords)
                .totalInvoices(totalInvoices)
                .ebmSuccess(ebmSuccess)
                .ebmPending(ebmPending)
                .ebmFailed(ebmFailed)
                .monthlyTrend(trend)
                .topPayers(topPayers)
                .paymentMethods(methods)
                .build();
    }

    // ── PDF Generation ────────────────────────────────────────────────────────

    public byte[] generatePdf(ReportData d, String generatedBy) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter   writer  = new PdfWriter(out);
        PdfDocument pdfDoc  = new PdfDocument(writer);
        pdfDoc.setDefaultPageSize(PageSize.A4);

        // Page event handler for header/footer lines on every page
        pdfDoc.addEventHandler(com.itextpdf.kernel.events.PdfDocumentEvent.END_PAGE,
                new PageHeaderFooter());

        Document doc = new Document(pdfDoc, PageSize.A4);
        doc.setMargins(72, 50, 60, 50);

        PdfFont bold    = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        PdfFont regular = PdfFontFactory.createFont(StandardFonts.HELVETICA);
        PdfFont light   = PdfFontFactory.createFont(StandardFonts.HELVETICA_OBLIQUE);

        // ── Cover / Header block ──────────────────────────────────────────────
        doc.add(new Paragraph("RentMIS")
                .setFont(bold).setFontSize(28).setFontColor(BRAND_DARK)
                .setMarginBottom(2));
        doc.add(new Paragraph("Business Analytics Report")
                .setFont(regular).setFontSize(14).setFontColor(BRAND_BLUE)
                .setMarginBottom(4));

        // Thin blue rule
        Table rule = new Table(UnitValue.createPercentArray(new float[]{100})).useAllAvailableWidth();
        rule.addCell(new Cell().setHeight(3).setBackgroundColor(BRAND_BLUE).setBorder(Border.NO_BORDER));
        doc.add(rule);

        // Meta row
        Table meta = new Table(UnitValue.createPercentArray(new float[]{50, 50})).useAllAvailableWidth()
                .setMarginTop(6).setMarginBottom(18);
        meta.addCell(noB(new Cell().add(new Paragraph("Generated: " + d.getGeneratedAt())
                .setFont(light).setFontSize(8).setFontColor(MID_GREY))));
        meta.addCell(noB(new Cell().add(new Paragraph("Period: " + d.getPeriod())
                .setFont(light).setFontSize(8).setFontColor(MID_GREY).setTextAlignment(TextAlignment.RIGHT))));
        doc.add(meta);

        // ── Section 1: Financial Summary ──────────────────────────────────────
        sectionTitle(doc, bold, "1. Financial Summary");

        Table kpi = new Table(UnitValue.createPercentArray(new float[]{25, 25, 25, 25})).useAllAvailableWidth()
                .setMarginBottom(14);
        kpi.addCell(kpiCell("Total Revenue", fmt(d.getTotalRevenue()), BRAND_BLUE, bold, regular));
        kpi.addCell(kpiCell("Annual Revenue", fmt(d.getAnnualRevenue()), BRAND_GREEN, bold, regular));
        kpi.addCell(kpiCell("Monthly Revenue", fmt(d.getMonthlyRevenue()), BRAND_DARK, bold, regular));
        kpi.addCell(kpiCell("Avg Payment", fmt(d.getAvgPaymentAmount()), MID_GREY, bold, regular));
        doc.add(kpi);

        Table payStats = new Table(UnitValue.createPercentArray(new float[]{33, 33, 34})).useAllAvailableWidth()
                .setMarginBottom(18);
        payStats.addCell(kpiCell("Completed Payments", String.valueOf(d.getCompletedPayments()), BRAND_GREEN, bold, regular));
        payStats.addCell(kpiCell("Pending Payments",   String.valueOf(d.getPendingPayments()),   BRAND_YELLOW, bold, regular));
        payStats.addCell(kpiCell("Failed Payments",    String.valueOf(d.getFailedPayments()),    BRAND_RED, bold, regular));
        doc.add(payStats);

        // ── Section 2: Monthly Revenue Trend ─────────────────────────────────
        sectionTitle(doc, bold, "2. Monthly Revenue Trend (Last 12 Months)");

        if (d.getMonthlyTrend() != null && !d.getMonthlyTrend().isEmpty()) {
            // Find max for bar scaling
            BigDecimal maxRev = d.getMonthlyTrend().stream()
                    .map(ReportData.MonthRow::getRevenue).max(BigDecimal::compareTo)
                    .orElse(BigDecimal.ONE);
            if (maxRev.compareTo(BigDecimal.ZERO) == 0) maxRev = BigDecimal.ONE;

            Table trendTable = new Table(UnitValue.createPercentArray(new float[]{20, 28, 12, 40}))
                    .useAllAvailableWidth().setMarginBottom(18);

            // Header
            for (String h : new String[]{"Month", "Revenue (RWF)", "Payments", "Bar"}) {
                trendTable.addHeaderCell(headerCell(h, bold));
            }

            for (ReportData.MonthRow row : d.getMonthlyTrend()) {
                trendTable.addCell(dataCell(row.getLabel(), regular));
                trendTable.addCell(dataCell(fmt(row.getRevenue()), regular).setTextAlignment(TextAlignment.RIGHT));
                trendTable.addCell(dataCell(String.valueOf(row.getCount()), regular).setTextAlignment(TextAlignment.CENTER));

                // Mini bar
                double pct = row.getRevenue().divide(maxRev, 4, RoundingMode.HALF_UP).doubleValue();
                Cell barCell = new Cell().setBorder(Border.NO_BORDER).setPadding(4);
                Table barTable = new Table(UnitValue.createPercentArray(new float[]{(float)(pct * 100), (float)((1 - pct) * 100 + 0.01)}))
                        .useAllAvailableWidth();
                barTable.addCell(new Cell().setHeight(10).setBackgroundColor(BRAND_BLUE).setBorder(Border.NO_BORDER));
                barTable.addCell(new Cell().setHeight(10).setBorder(Border.NO_BORDER));
                barCell.add(barTable);
                trendTable.addCell(barCell);
            }
            doc.add(trendTable);
        } else {
            doc.add(new Paragraph("No revenue data available for this period.")
                    .setFont(light).setFontSize(9).setFontColor(MID_GREY).setMarginBottom(18));
        }

        // ── Section 3: Occupancy & Portfolio ─────────────────────────────────
        sectionTitle(doc, bold, "3. Occupancy & Portfolio");

        Table occTable = new Table(UnitValue.createPercentArray(new float[]{50, 50})).useAllAvailableWidth()
                .setMarginBottom(18);

        // Left: occupancy stats
        Cell leftCell = new Cell().setBorder(Border.NO_BORDER).setPadding(0).setPaddingRight(8);
        Table occStats = new Table(UnitValue.createPercentArray(new float[]{60, 40})).useAllAvailableWidth();
        occStats.addHeaderCell(headerCell("Metric", bold));
        occStats.addHeaderCell(headerCell("Value", bold));
        addRow(occStats, "Total Properties", String.valueOf(d.getTotalProperties()), regular);
        addRow(occStats, "Total Units",      String.valueOf(d.getTotalUnits()),      regular);
        addRow(occStats, "Occupied Units",   String.valueOf(d.getOccupiedUnits()),   regular);
        addRow(occStats, "Available Units",  String.valueOf(d.getAvailableUnits()),  regular);
        addRow(occStats, "Occupancy Rate",   d.getOccupancyRate() + "%",            regular);
        leftCell.add(occStats);
        occTable.addCell(leftCell);

        // Right: visual occupancy gauge
        Cell rightCell = new Cell().setBorder(Border.NO_BORDER).setPadding(0).setPaddingLeft(8);
        double occPct = d.getOccupancyRate() / 100.0;
        Table gauge = new Table(UnitValue.createPercentArray(new float[]{(float)(occPct * 100 + 0.01), (float)((1 - occPct) * 100 + 0.01)}))
                .useAllAvailableWidth().setMarginTop(8);
        gauge.addCell(new Cell().setHeight(20).setBackgroundColor(BRAND_GREEN).setBorder(Border.NO_BORDER));
        gauge.addCell(new Cell().setHeight(20).setBackgroundColor(LIGHT_GREY).setBorder(Border.NO_BORDER));
        rightCell.add(new Paragraph("Occupancy").setFont(bold).setFontSize(9).setFontColor(BRAND_DARK));
        rightCell.add(gauge);
        rightCell.add(new Paragraph(d.getOccupancyRate() + "% occupied   |   " + d.getAvailableUnits() + " available")
                .setFont(light).setFontSize(8).setFontColor(MID_GREY).setMarginTop(4));
        occTable.addCell(rightCell);
        doc.add(occTable);

        // ── Section 4: Contracts ──────────────────────────────────────────────
        sectionTitle(doc, bold, "4. Contract Status");

        Table contractTable = new Table(UnitValue.createPercentArray(new float[]{40, 20, 40}))
                .useAllAvailableWidth().setMarginBottom(18);
        contractTable.addHeaderCell(headerCell("Status", bold));
        contractTable.addHeaderCell(headerCell("Count", bold));
        contractTable.addHeaderCell(headerCell("Share", bold));
        long totalC = d.getTotalContracts() > 0 ? d.getTotalContracts() : 1;
        addContractRow(contractTable, "Active",            d.getActiveContracts(),           totalC, BRAND_GREEN, regular);
        addContractRow(contractTable, "Pending Signature", d.getPendingSignatureContracts(), totalC, BRAND_YELLOW, regular);
        addContractRow(contractTable, "Expired",           d.getExpiredContracts(),          totalC, MID_GREY, regular);
        addContractRow(contractTable, "Terminated",        d.getTerminatedContracts(),       totalC, BRAND_RED, regular);

        // Total row
        contractTable.addCell(new Cell().add(new Paragraph("TOTAL")
                .setFont(bold).setFontSize(8).setFontColor(BRAND_DARK))
                .setBackgroundColor(TABLE_HEADER).setBorderTop(new SolidBorder(BRAND_DARK, 0.5f)));
        contractTable.addCell(new Cell().add(new Paragraph(String.valueOf(d.getTotalContracts()))
                .setFont(bold).setFontSize(8).setFontColor(BRAND_DARK).setTextAlignment(TextAlignment.RIGHT))
                .setBackgroundColor(TABLE_HEADER).setBorderTop(new SolidBorder(BRAND_DARK, 0.5f)));
        contractTable.addCell(new Cell().add(new Paragraph("100%")
                .setFont(bold).setFontSize(8).setFontColor(BRAND_DARK))
                .setBackgroundColor(TABLE_HEADER).setBorderTop(new SolidBorder(BRAND_DARK, 0.5f)));
        doc.add(contractTable);

        // ── Section 5: Top Payers ─────────────────────────────────────────────
        sectionTitle(doc, bold, "5. Top Payers");

        if (d.getTopPayers() != null && !d.getTopPayers().isEmpty()) {
            Table topTable = new Table(UnitValue.createPercentArray(new float[]{10, 40, 20, 30}))
                    .useAllAvailableWidth().setMarginBottom(18);
            topTable.addHeaderCell(headerCell("#", bold));
            topTable.addHeaderCell(headerCell("Tenant", bold));
            topTable.addHeaderCell(headerCell("Payments", bold));
            topTable.addHeaderCell(headerCell("Total Paid (RWF)", bold));
            int rank = 1;
            for (ReportData.TopPayer p : d.getTopPayers()) {
                topTable.addCell(dataCell(String.valueOf(rank++), regular).setTextAlignment(TextAlignment.CENTER));
                topTable.addCell(dataCell(p.getTenantName(), regular));
                topTable.addCell(dataCell(String.valueOf(p.getPaymentCount()), regular).setTextAlignment(TextAlignment.CENTER));
                topTable.addCell(dataCell(fmt(p.getTotalPaid()), regular).setTextAlignment(TextAlignment.RIGHT));
            }
            doc.add(topTable);
        } else {
            doc.add(new Paragraph("No payment data available.")
                    .setFont(light).setFontSize(9).setFontColor(MID_GREY).setMarginBottom(18));
        }

        // ── Section 6: Payment Methods ────────────────────────────────────────
        sectionTitle(doc, bold, "6. Payment Methods");

        if (d.getPaymentMethods() != null && !d.getPaymentMethods().isEmpty()) {
            Table methTable = new Table(UnitValue.createPercentArray(new float[]{35, 20, 30, 15}))
                    .useAllAvailableWidth().setMarginBottom(18);
            methTable.addHeaderCell(headerCell("Method", bold));
            methTable.addHeaderCell(headerCell("Count", bold));
            methTable.addHeaderCell(headerCell("Total (RWF)", bold));
            methTable.addHeaderCell(headerCell("Share", bold));
            long totalPay = d.getPaymentMethods().stream().mapToLong(ReportData.MethodRow::getCount).sum();
            if (totalPay == 0) totalPay = 1;
            for (ReportData.MethodRow m : d.getPaymentMethods()) {
                double share = (double) m.getCount() / totalPay * 100;
                methTable.addCell(dataCell(m.getMethod(), regular));
                methTable.addCell(dataCell(String.valueOf(m.getCount()), regular).setTextAlignment(TextAlignment.CENTER));
                methTable.addCell(dataCell(fmt(m.getTotal()), regular).setTextAlignment(TextAlignment.RIGHT));
                methTable.addCell(dataCell(String.format("%.1f%%", share), regular).setTextAlignment(TextAlignment.CENTER));
            }
            doc.add(methTable);
        }

        // ── Section 7: EBM / Invoices ─────────────────────────────────────────
        sectionTitle(doc, bold, "7. Invoices & EBM Status");

        Table ebmTable = new Table(UnitValue.createPercentArray(new float[]{40, 20, 40}))
                .useAllAvailableWidth().setMarginBottom(18);
        ebmTable.addHeaderCell(headerCell("Status", bold));
        ebmTable.addHeaderCell(headerCell("Count", bold));
        ebmTable.addHeaderCell(headerCell("Notes", bold));
        long totalInv = d.getTotalInvoices() > 0 ? d.getTotalInvoices() : 1;
        addEbmRow(ebmTable, "SUCCESS", d.getEbmSuccess(), totalInv, BRAND_GREEN, regular);
        addEbmRow(ebmTable, "PENDING", d.getEbmPending(), totalInv, BRAND_YELLOW, regular);
        addEbmRow(ebmTable, "FAILED",  d.getEbmFailed(),  totalInv, BRAND_RED, regular);
        doc.add(ebmTable);

        // ── Footer note ───────────────────────────────────────────────────────
        doc.add(new Paragraph()
                .add(new Text("This report was generated automatically by RentMIS on " + d.getGeneratedAt() + ". ")
                        .setFont(light).setFontSize(7).setFontColor(MID_GREY))
                .add(new Text("© " + java.time.Year.now().getValue() + " Good Link Solutions. All rights reserved.")
                        .setFont(light).setFontSize(7).setFontColor(MID_GREY))
                .setMarginTop(20).setTextAlignment(TextAlignment.CENTER));

        doc.close();
        return out.toByteArray();
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private static BigDecimal nvl(BigDecimal v) { return v != null ? v : BigDecimal.ZERO; }
    private static Long nvlL(Long v) { return v != null ? v : 0L; }

    private static String fmt(BigDecimal v) {
        if (v == null) return "RWF 0";
        return "RWF " + NumberFormat.getNumberInstance(Locale.US).format(v.setScale(0, RoundingMode.HALF_UP));
    }

    private void sectionTitle(Document doc, PdfFont bold, String title) {
        doc.add(new Paragraph(title)
                .setFont(bold).setFontSize(11).setFontColor(BRAND_DARK)
                .setMarginTop(8).setMarginBottom(6)
                .setBorderBottom(new SolidBorder(BRAND_BLUE, 1.5f)).setPaddingBottom(3));
    }

    private Cell kpiCell(String label, String value, DeviceRgb accentColor,
                         PdfFont bold, PdfFont regular) {
        Cell cell = new Cell().setBackgroundColor(LIGHT_GREY)
                .setBorder(Border.NO_BORDER)
                .setBorderLeft(new SolidBorder(accentColor, 3))
                .setPadding(8).setMargin(3);
        cell.add(new Paragraph(value).setFont(bold).setFontSize(11).setFontColor(accentColor).setMarginBottom(2));
        cell.add(new Paragraph(label).setFont(regular).setFontSize(7).setFontColor(MID_GREY));
        return cell;
    }

    private Cell headerCell(String text, PdfFont bold) {
        return new Cell().add(new Paragraph(text).setFont(bold).setFontSize(8).setFontColor(BRAND_DARK))
                .setBackgroundColor(TABLE_HEADER)
                .setBorderBottom(new SolidBorder(BRAND_BLUE, 1))
                .setBorderTop(Border.NO_BORDER).setBorderLeft(Border.NO_BORDER).setBorderRight(Border.NO_BORDER)
                .setPadding(5);
    }

    private Cell dataCell(String text, PdfFont regular) {
        return new Cell().add(new Paragraph(text).setFont(regular).setFontSize(8).setFontColor(BRAND_DARK))
                .setBorderBottom(new SolidBorder(LIGHT_GREY, 0.5f))
                .setBorderTop(Border.NO_BORDER).setBorderLeft(Border.NO_BORDER).setBorderRight(Border.NO_BORDER)
                .setPadding(5);
    }

    private Cell noB(Cell c) { return c.setBorder(Border.NO_BORDER); }

    private void addRow(Table t, String label, String value, PdfFont regular) {
        t.addCell(new Cell().add(new Paragraph(label).setFont(regular).setFontSize(8).setFontColor(BRAND_DARK))
                .setBorderBottom(new SolidBorder(LIGHT_GREY, 0.5f)).setBorderTop(Border.NO_BORDER)
                .setBorderLeft(Border.NO_BORDER).setBorderRight(Border.NO_BORDER).setPadding(4));
        t.addCell(new Cell().add(new Paragraph(value).setFont(regular).setFontSize(8)
                .setFontColor(BRAND_BLUE).setTextAlignment(TextAlignment.RIGHT))
                .setBorderBottom(new SolidBorder(LIGHT_GREY, 0.5f)).setBorderTop(Border.NO_BORDER)
                .setBorderLeft(Border.NO_BORDER).setBorderRight(Border.NO_BORDER).setPadding(4));
    }

    private void addContractRow(Table t, String status, Long count, long total,
                                DeviceRgb color, PdfFont regular) {
        double pct = (double) count / total * 100;
        t.addCell(new Cell().add(new Paragraph(status).setFont(regular).setFontSize(8))
                .setBorderBottom(new SolidBorder(LIGHT_GREY, 0.5f)).setBorderTop(Border.NO_BORDER)
                .setBorderLeft(new SolidBorder(color, 3)).setBorderRight(Border.NO_BORDER).setPadding(5));
        t.addCell(new Cell().add(new Paragraph(String.valueOf(count)).setFont(regular).setFontSize(8)
                .setTextAlignment(TextAlignment.RIGHT))
                .setBorderBottom(new SolidBorder(LIGHT_GREY, 0.5f)).setBorderTop(Border.NO_BORDER)
                .setBorderLeft(Border.NO_BORDER).setBorderRight(Border.NO_BORDER).setPadding(5));
        // Share bar cell
        Cell barCell = new Cell().setBorder(Border.NO_BORDER).setPadding(4);
        Table bar = new Table(UnitValue.createPercentArray(new float[]{(float)(pct + 0.01), (float)(100 - pct + 0.01)}))
                .useAllAvailableWidth();
        bar.addCell(new Cell().setHeight(10).setBackgroundColor(color).setBorder(Border.NO_BORDER));
        bar.addCell(new Cell().setHeight(10).setBackgroundColor(LIGHT_GREY).setBorder(Border.NO_BORDER));
        barCell.add(bar);
        barCell.add(new Paragraph(String.format("%.1f%%", pct)).setFont(regular).setFontSize(7)
                .setFontColor(MID_GREY));
        t.addCell(barCell);
    }

    private void addEbmRow(Table t, String status, Long count, long total,
                           DeviceRgb color, PdfFont regular) {
        double pct = (double) count / total * 100;
        String note = switch (status) {
            case "SUCCESS" -> "EBM receipt generated and verified";
            case "PENDING" -> "Awaiting landlord action";
            case "FAILED"  -> "Retry required";
            default        -> "–";
        };
        t.addCell(new Cell().add(new Paragraph(status).setFont(regular).setFontSize(8).setFontColor(color))
                .setBorderBottom(new SolidBorder(LIGHT_GREY, 0.5f)).setBorderTop(Border.NO_BORDER)
                .setBorderLeft(new SolidBorder(color, 3)).setBorderRight(Border.NO_BORDER).setPadding(5));
        t.addCell(new Cell().add(new Paragraph(String.format("%d (%.1f%%)", count, pct))
                .setFont(regular).setFontSize(8).setTextAlignment(TextAlignment.CENTER))
                .setBorderBottom(new SolidBorder(LIGHT_GREY, 0.5f)).setBorderTop(Border.NO_BORDER)
                .setBorderLeft(Border.NO_BORDER).setBorderRight(Border.NO_BORDER).setPadding(5));
        t.addCell(new Cell().add(new Paragraph(note).setFont(regular).setFontSize(8).setFontColor(MID_GREY))
                .setBorderBottom(new SolidBorder(LIGHT_GREY, 0.5f)).setBorderTop(Border.NO_BORDER)
                .setBorderLeft(Border.NO_BORDER).setBorderRight(Border.NO_BORDER).setPadding(5));
    }

    // ── Page header / footer handler ──────────────────────────────────────────

    private static class PageHeaderFooter implements com.itextpdf.kernel.events.IEventHandler {
        @Override
        public void handleEvent(com.itextpdf.kernel.events.Event event) {
            com.itextpdf.kernel.events.PdfDocumentEvent docEvent =
                    (com.itextpdf.kernel.events.PdfDocumentEvent) event;
            PdfDocument      pdfDoc  = docEvent.getDocument();
            PdfPage          page    = docEvent.getPage();
            int              pageNum = pdfDoc.getPageNumber(page);
            Rectangle        rect    = page.getPageSize();
            PdfCanvas        canvas  = new PdfCanvas(page.newContentStreamBefore(), page.getResources(), pdfDoc);

            try {
                PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA);
                PdfFont bold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);

                // Top blue band
                canvas.saveState()
                        .setFillColor(BRAND_DARK)
                        .rectangle(0, rect.getHeight() - 28, rect.getWidth(), 28)
                        .fill()
                        .setFillColor(ColorConstants.WHITE)
                        .beginText()
                        .setFontAndSize(bold, 9)
                        .moveText(50, rect.getHeight() - 18)
                        .showText("RentMIS — Business Analytics Report")
                        .endText()
                        .setFillColor(new DeviceRgb(140, 153, 179))
                        .beginText()
                        .setFontAndSize(font, 8)
                        .moveText(rect.getWidth() - 80, rect.getHeight() - 18)
                        .showText("Page " + pageNum + " of " + pdfDoc.getNumberOfPages())
                        .endText()
                        .restoreState();

                // Bottom footer line
                canvas.saveState()
                        .setStrokeColor(BRAND_BLUE)
                        .setLineWidth(0.5f)
                        .moveTo(50, 40)
                        .lineTo(rect.getWidth() - 50, 40)
                        .stroke()
                        .setFillColor(MID_GREY)
                        .beginText()
                        .setFontAndSize(font, 7)
                        .moveText(50, 28)
                        .showText("© " + java.time.Year.now().getValue() + " Good Link Solutions. All rights reserved. Confidential.")
                        .endText()
                        .restoreState();
            } catch (Exception ignored) {}
            canvas.release();
        }
    }
}
