package com.axenovo.infotech.controller;

import com.axenovo.infotech.model.ContactInquiryForm;
import com.axenovo.infotech.service.PortfolioItemService;
import com.axenovo.infotech.service.ServiceItemService;
import com.axenovo.infotech.service.SiteSettingService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.LinkedHashMap;
import java.util.Map;

@Controller
public class Home {

    private final SiteSettingService siteSettingService;
    private final ServiceItemService serviceItemService;
    private final PortfolioItemService portfolioItemService;

    public Home(
        SiteSettingService siteSettingService,
        ServiceItemService serviceItemService,
        PortfolioItemService portfolioItemService
    ) {
        this.siteSettingService = siteSettingService;
        this.serviceItemService = serviceItemService;
        this.portfolioItemService = portfolioItemService;
    }

    @GetMapping({"/", "/home"})
    public String home(Model model) {
        String maintenanceView = maintenanceViewIfEnabled(model);
        if (maintenanceView != null) {
            return maintenanceView;
        }

        ensureContactForm(model);
        addSharedContent(model);
        model.addAttribute("homeContent", siteSettingService.getMany(defaultHomeSettings()));
        model.addAttribute("serviceItems", serviceItemService.findAll());
        model.addAttribute("portfolioItems", portfolioItemService.findAll());
        return "home";
    }

    @GetMapping("/portfolio")
    public String portfolio(Model model) {
        String maintenanceView = maintenanceViewIfEnabled(model);
        if (maintenanceView != null) {
            return maintenanceView;
        }

        addSharedContent(model);
        model.addAttribute("portfolioItems", portfolioItemService.findAll());
        return "portfolio";
    }

    @GetMapping("/about")
    public String about(Model model) {
        String maintenanceView = maintenanceViewIfEnabled(model);
        if (maintenanceView != null) {
            return maintenanceView;
        }

        addSharedContent(model);
        model.addAttribute("aboutContent", siteSettingService.getMany(defaultAboutSettings()));
        return "about";
    }

    @GetMapping("/services")
    public String services(Model model) {
        String maintenanceView = maintenanceViewIfEnabled(model);
        if (maintenanceView != null) {
            return maintenanceView;
        }

        addSharedContent(model);
        model.addAttribute("serviceItems", serviceItemService.findAll());
        return "services";
    }

    @GetMapping("/maintenance")
    public String maintenance(Model model) {
        if (!siteSettingService.isMaintenanceModeEnabled()) {
            return "redirect:/home";
        }

        model.addAttribute("maintenanceMessage", siteSettingService.getMaintenanceMessage());
        return "maintenance";
    }

    private void ensureContactForm(Model model) {
        if (!model.containsAttribute("contactForm")) {
            model.addAttribute("contactForm", new ContactInquiryForm());
        }
    }

    private void addSharedContent(Model model) {
        model.addAttribute("contactContent", siteSettingService.getMany(defaultContactSettings()));
        model.addAttribute("footerContent", siteSettingService.getMany(defaultFooterSettings()));
    }

    private String maintenanceViewIfEnabled(Model model) {
        if (!siteSettingService.isMaintenanceModeEnabled()) {
            return null;
        }

        model.addAttribute("maintenanceMessage", siteSettingService.getMaintenanceMessage());
        return "maintenance";
    }

    private Map<String, String> defaultHomeSettings() {
        Map<String, String> defaults = new LinkedHashMap<>();
        defaults.put("home.hero.kicker", "Building scalable technology. Designing powerful brands.");
        defaults.put("home.hero.title", "Empowering Your Digital Evolution.");
        defaults.put("home.hero.lead", "Axenovo Infotech helps startups, enterprises, and growing businesses launch secure software, modern web experiences, and brand systems that drive measurable growth.");
        defaults.put("home.hero.topEyebrow", "Strategy to launch");
        defaults.put("home.hero.topText", "Software, web, design, and digital media under one roof");
        defaults.put("home.hero.bottomEyebrow", "Future-ready systems");
        defaults.put("home.hero.bottomText", "Secure, scalable, and performance-focused delivery built for long-term growth.");
        return defaults;
    }

    private Map<String, String> defaultAboutSettings() {
        Map<String, String> defaults = new LinkedHashMap<>();
        defaults.put("about.hero.kicker", "About Axenovo");
        defaults.put("about.hero.title", "Technology delivery built around clarity, reliability, and long-term growth.");
        defaults.put("about.hero.lead", "Axenovo Infotech Pvt. Ltd. is a technology-driven IT company focused on building secure, scalable digital solutions while keeping communication, design, and business outcomes aligned.");
        defaults.put("about.section.kicker", "Who We Are");
        defaults.put("about.section.title", "Technology should not just support your business. It should accelerate it.");
        defaults.put("about.section.para1", "Axenovo Infotech Pvt. Ltd. is committed to delivering secure, scalable, and future-ready digital solutions. Our work combines strong technical execution with design clarity and strategic thinking.");
        defaults.put("about.section.para2", "From backend-heavy software systems and modern web applications to digital branding and media, we help businesses innovate, scale, and lead in the digital era.");
        defaults.put("about.mission", "To empower businesses with reliable, scalable, and future-ready technology solutions that help them compete globally.");
        defaults.put("about.vision", "To become a globally recognized IT solutions provider known for innovation, quality, and long-term client partnerships.");
        return defaults;
    }

    private Map<String, String> defaultContactSettings() {
        Map<String, String> defaults = new LinkedHashMap<>();
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
        return defaults;
    }

    private Map<String, String> defaultFooterSettings() {
        Map<String, String> defaults = new LinkedHashMap<>();
        defaults.put("footer.tagline", "Scalable technology and digital growth solutions");
        defaults.put("footer.description", "We design, build, and launch digital systems that help businesses move with more confidence and speed.");
        defaults.put("footer.companyHeading", "Company");
        defaults.put("footer.reachHeading", "Reach Us");
        defaults.put("footer.bottomText", "Axenovo Infotech Pvt. Ltd. All rights reserved.");
        return defaults;
    }
}
