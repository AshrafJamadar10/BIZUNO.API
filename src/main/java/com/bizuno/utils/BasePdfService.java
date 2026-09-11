package com.bizuno.utils;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.Margin;
import com.microsoft.playwright.options.WaitUntilState;

import java.nio.file.Files;
import java.nio.file.Path;

public abstract class BasePdfService {

    protected byte[] htmlToPdf(String html) {
        Path tempPdf = null;
        try {
            tempPdf = Files.createTempFile("output", ".pdf");

            try (Playwright playwright = Playwright.create()) {
                Browser browser = playwright.chromium().launch(
                        new BrowserType.LaunchOptions().setHeadless(true)
                );
                try {
                    Page page = browser.newPage();

                    // Load HTML content and wait for network (images/fonts) to finish
                    page.setContent(html, new Page.SetContentOptions()
                            .setWaitUntil(WaitUntilState.NETWORKIDLE));

                    // Give web fonts a moment to finish shaping/rendering
                    page.waitForTimeout(300);

                    page.pdf(new Page.PdfOptions()
                            .setPath(tempPdf)
                            .setPreferCSSPageSize(true)   // <-- respect your @page size, not A4
                            .setPrintBackground(true)
                            .setMargin(new Margin().setTop("0").setBottom("0").setLeft("0").setRight("0")));
                } finally {
                    browser.close();
                }
            }

            return Files.readAllBytes(tempPdf);

        } catch (Exception e) {
            throw new RuntimeException("Failed to convert HTML to PDF", e);
        } finally {
            if (tempPdf != null) {
                try {
                    Files.deleteIfExists(tempPdf);
                } catch (Exception ignored) {
                    // best-effort cleanup
                }
            }
        }
    }
}
