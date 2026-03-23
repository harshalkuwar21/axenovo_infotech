package com.axenovo.infotech.config;

import com.axenovo.infotech.entity.PortfolioItem;
import com.axenovo.infotech.entity.ServiceItem;
import com.axenovo.infotech.service.PortfolioItemService;
import com.axenovo.infotech.service.ServiceItemService;
import com.axenovo.infotech.service.SiteSettingService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class AdminContentSeeder implements CommandLineRunner {

    private final SiteSettingService siteSettingService;
    private final ServiceItemService serviceItemService;
    private final PortfolioItemService portfolioItemService;

    public AdminContentSeeder(
        SiteSettingService siteSettingService,
        ServiceItemService serviceItemService,
        PortfolioItemService portfolioItemService
    ) {
        this.siteSettingService = siteSettingService;
        this.serviceItemService = serviceItemService;
        this.portfolioItemService = portfolioItemService;
    }

    @Override
    public void run(String... args) {
        seedSettings();
        seedServices();
        seedPortfolio();
    }

    private void seedSettings() {
        Map<String, String> defaults = new LinkedHashMap<>();
        defaults.put("home.hero.kicker", "Building scalable technology. Designing powerful brands.");
        defaults.put("home.hero.title", "Empowering Your Digital Evolution.");
        defaults.put("home.hero.lead", "Axenovo Infotech helps startups, enterprises, and growing businesses launch secure software, modern web experiences, and brand systems that drive measurable growth.");
        defaults.put("home.hero.topEyebrow", "Strategy to launch");
        defaults.put("home.hero.topText", "Software, web, design, and digital media under one roof");
        defaults.put("home.hero.bottomEyebrow", "Future-ready systems");
        defaults.put("home.hero.bottomText", "Secure, scalable, and performance-focused delivery built for long-term growth.");

        defaults.put("about.hero.kicker", "About Axenovo");
        defaults.put("about.hero.title", "Technology delivery built around clarity, reliability, and long-term growth.");
        defaults.put("about.hero.lead", "Axenovo Infotech Pvt. Ltd. is a technology-driven IT company focused on building secure, scalable digital solutions while keeping communication, design, and business outcomes aligned.");
        defaults.put("about.section.kicker", "Who We Are");
        defaults.put("about.section.title", "Technology should not just support your business. It should accelerate it.");
        defaults.put("about.section.para1", "Axenovo Infotech Pvt. Ltd. is committed to delivering secure, scalable, and future-ready digital solutions. Our work combines strong technical execution with design clarity and strategic thinking.");
        defaults.put("about.section.para2", "From backend-heavy software systems and modern web applications to digital branding and media, we help businesses innovate, scale, and lead in the digital era.");
        defaults.put("about.mission", "To empower businesses with reliable, scalable, and future-ready technology solutions that help them compete globally.");
        defaults.put("about.vision", "To become a globally recognized IT solutions provider known for innovation, quality, and long-term client partnerships.");

        defaults.put("contact.hero.kicker", "Contact");
        defaults.put("contact.hero.title", "Let's talk about your next software, web, or design project.");
        defaults.put("contact.hero.lead", "Whether you are planning a new product, improving an existing platform, or building your digital presence, we are ready to connect.");
        defaults.put("contact.section.kicker", "Start the Conversation");
        defaults.put("contact.section.title", "Share your goals and we will help you plan the next step.");
        defaults.put("contact.section.lead", "Send us your project details, preferred scope, and timeline. We will review your inquiry and get back to you with the right next step.");
        defaults.put("contact.email", "axenovoinfotech@gmail.com");
        defaults.put("contact.phone1", "+91 70587 92757");
        defaults.put("contact.phone2", "+91 99702 48871");
        defaults.put("contact.location", "Nashik, Maharashtra, India");

        siteSettingService.saveAll(defaults);
    }

    private void seedServices() {
        if (!serviceItemService.findAll().isEmpty()) {
            return;
        }

        List<ServiceItem> defaults = List.of(
            createService("Custom Software Development", "Tailored enterprise software, SaaS platforms, CRM and ERP systems, backend architecture, API integrations, and secure database management.", "Scalable by design", "software", 1),
            createService("Web Application Development", "Responsive company websites, custom web apps, admin dashboards, portals, e-commerce experiences, and progressive web applications.", "Fast, secure, responsive", "web", 2),
            createService("UI/UX and Graphic Design", "Brand identity systems, logo design, brochures, social creatives, website interfaces, and pitch-ready presentation assets.", "Clear and memorable design", "design", 3),
            createService("Video Editing and Digital Media", "Promotional videos, reels, corporate presentations, ad edits, and brand story content built to increase engagement across platforms.", "Storytelling with strategy", "media", 4),
            createService("Branding and Digital Strategy", "Brand positioning, market insight, competitor analysis, visual systems, and growth strategy that sharpen your market presence.", "Built for business trust", "strategy", 5)
        );
        defaults.forEach(serviceItemService::save);
    }

    private void seedPortfolio() {
        if (!portfolioItemService.findAll().isEmpty()) {
            return;
        }

        List<PortfolioItem> defaults = List.of(
            createPortfolio("SaaS HR Showcase Website", "Responsive hiring showcase with product-led storytelling and conversion-focused sections.", "Jan 2025", "Published", "/assets/graphics/hero-tech.svg", 1),
            createPortfolio("Axenovo Corporate Website", "Brand-first company website with service storytelling and lead capture touchpoints.", "Dec 2024", "Live", "/assets/graphics/digital-team.svg", 2),
            createPortfolio("Cybersecurity Solution Campaign", "Launch microsite and campaign assets built to communicate trust, speed, and compliance.", "Nov 2024", "Review", "/assets/graphics/team-collaboration.svg", 3)
        );
        defaults.forEach(portfolioItemService::save);
    }

    private ServiceItem createService(String title, String description, String linkLabel, String iconName, int order) {
        ServiceItem item = new ServiceItem();
        item.setTitle(title);
        item.setDescription(description);
        item.setLinkLabel(linkLabel);
        item.setIconName(iconName);
        item.setSortOrder(order);
        return item;
    }

    private PortfolioItem createPortfolio(String title, String summary, String dateLabel, String status, String imagePath, int order) {
        PortfolioItem item = new PortfolioItem();
        item.setTitle(title);
        item.setSummary(summary);
        item.setDateLabel(dateLabel);
        item.setStatus(status);
        item.setImagePath(imagePath);
        item.setSortOrder(order);
        return item;
    }
}
