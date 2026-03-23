package com.axenovo.infotech.controller;

import com.axenovo.infotech.entity.PortfolioItem;
import com.axenovo.infotech.entity.ServiceItem;
import com.axenovo.infotech.model.AdminLoginForm;
import com.axenovo.infotech.model.AdminNavItem;
import com.axenovo.infotech.model.AdminSectionCard;
import com.axenovo.infotech.model.AdminStatCard;
import com.axenovo.infotech.service.ContactInquiryService;
import com.axenovo.infotech.service.PortfolioItemService;
import com.axenovo.infotech.service.ServiceItemService;
import com.axenovo.infotech.service.SiteSettingService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private static final String SESSION_ADMIN_AUTH = "adminAuthenticated";
    private static final String NAV_DASHBOARD = "dashboard";
    private static final String NAV_HOME = "home";
    private static final String NAV_ABOUT = "about";
    private static final String NAV_SERVICES = "services";
    private static final String NAV_PORTFOLIO = "portfolio";
    private static final String NAV_CONTACT = "contact";
    private static final String NAV_INQUIRIES = "inquiries";
    private static final String DEFAULT_PORTFOLIO_IMAGE = "/assets/graphics/hero-tech.svg";
    private static final Path PORTFOLIO_UPLOAD_DIRECTORY = Paths.get("uploads", "portfolio");

    private final ContactInquiryService contactInquiryService;
    private final SiteSettingService siteSettingService;
    private final ServiceItemService serviceItemService;
    private final PortfolioItemService portfolioItemService;

    @Value("${axenovo.admin.username:admin@axenovo.com}")
    private String adminUsername;

    @Value("${axenovo.admin.password:Admin@123}")
    private String adminPassword;

    @Value("${axenovo.admin.name:Admin User}")
    private String adminDisplayName;

    public AdminController(
        ContactInquiryService contactInquiryService,
        SiteSettingService siteSettingService,
        ServiceItemService serviceItemService,
        PortfolioItemService portfolioItemService
    ) {
        this.contactInquiryService = contactInquiryService;
        this.siteSettingService = siteSettingService;
        this.serviceItemService = serviceItemService;
        this.portfolioItemService = portfolioItemService;
    }

    @GetMapping("/login")
    public String login(Model model, HttpSession session) {
        if (isAuthenticated(session)) {
            return "redirect:/admin/dashboard";
        }
        if (!model.containsAttribute("loginForm")) {
            model.addAttribute("loginForm", new AdminLoginForm());
        }
        model.addAttribute("loginHint", adminUsername);
        model.addAttribute("loginMetrics", buildLoginMetrics());
        return "admin/login";
    }

    @PostMapping("/login")
    public String handleLogin(
        @ModelAttribute("loginForm") AdminLoginForm loginForm,
        RedirectAttributes redirectAttributes,
        HttpSession session
    ) {
        String username = clean(loginForm.getUsername());
        String password = clean(loginForm.getPassword());

        if (username.isBlank() || password.isBlank()) {
            redirectAttributes.addFlashAttribute("loginError", "Please enter both username and password.");
            redirectAttributes.addFlashAttribute("loginForm", loginForm);
            return "redirect:/admin/login";
        }

        if (!adminUsername.equalsIgnoreCase(username) || !adminPassword.equals(password)) {
            redirectAttributes.addFlashAttribute("loginError", "Invalid admin credentials. Please try again.");
            redirectAttributes.addFlashAttribute("loginForm", loginForm);
            return "redirect:/admin/login";
        }

        session.setAttribute(SESSION_ADMIN_AUTH, Boolean.TRUE);
        redirectAttributes.addFlashAttribute("loginSuccess", "Welcome back, admin.");
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpSession session) {
        if (!isAuthenticated(session)) {
            return "redirect:/admin/login";
        }

        populateAdminLayout(model, NAV_DASHBOARD, "Admin Dashboard", "Home / Admin / Dashboard");
        model.addAttribute("statCards", buildStatCards());
        model.addAttribute("managementCards", buildManagementCards());
        model.addAttribute("recentInquiries", contactInquiryService.findRecent());
        model.addAttribute("footerContent", siteSettingService.getMany(defaultFooterSettings()));
        model.addAttribute("maintenanceEnabled", siteSettingService.isMaintenanceModeEnabled());
        model.addAttribute("maintenanceMessage", siteSettingService.getMaintenanceMessage());
        return "admin/dashboard";
    }

    @GetMapping("/home-content")
    public String homeContentPage(Model model, HttpSession session) {
        if (!isAuthenticated(session)) {
            return "redirect:/admin/login";
        }

        populateAdminLayout(model, NAV_HOME, "Home Content", "Home / Admin / Home Content");
        model.addAttribute("homeContent", siteSettingService.getMany(defaultHomeSettings()));
        return "admin/home-content";
    }

    @GetMapping("/about-content")
    public String aboutContentPage(Model model, HttpSession session) {
        if (!isAuthenticated(session)) {
            return "redirect:/admin/login";
        }

        populateAdminLayout(model, NAV_ABOUT, "About Content", "Home / Admin / About Content");
        model.addAttribute("aboutContent", siteSettingService.getMany(defaultAboutSettings()));
        return "admin/about-content";
    }

    @GetMapping("/services")
    public String servicesPage(Model model, HttpSession session) {
        if (!isAuthenticated(session)) {
            return "redirect:/admin/login";
        }

        populateAdminLayout(model, NAV_SERVICES, "Services Manager", "Home / Admin / Services");
        model.addAttribute("serviceItems", serviceItemService.findAll());
        model.addAttribute("serviceIcons", availableServiceIcons());
        return "admin/services";
    }

    @GetMapping("/portfolio")
    public String portfolioPage(Model model, HttpSession session) {
        if (!isAuthenticated(session)) {
            return "redirect:/admin/login";
        }

        populateAdminLayout(model, NAV_PORTFOLIO, "Portfolio Manager", "Home / Admin / Portfolio");
        model.addAttribute("portfolioItems", portfolioItemService.findAll());
        return "admin/portfolio";
    }

    @GetMapping("/contact")
    public String contactPage(Model model, HttpSession session) {
        if (!isAuthenticated(session)) {
            return "redirect:/admin/login";
        }

        populateAdminLayout(model, NAV_CONTACT, "Contact Content", "Home / Admin / Contact");
        model.addAttribute("contactContent", siteSettingService.getMany(defaultContactSettings()));
        return "admin/contact-content";
    }

    @GetMapping("/inquiries")
    public String inquiriesPage(Model model, HttpSession session) {
        if (!isAuthenticated(session)) {
            return "redirect:/admin/login";
        }

        populateAdminLayout(model, NAV_INQUIRIES, "Inquiry Inbox", "Home / Admin / Inquiries");
        model.addAttribute("recentInquiries", contactInquiryService.findRecent());
        model.addAttribute("totalInquiryCount", contactInquiryService.countAll());
        model.addAttribute("weeklyInquiryCount", contactInquiryService.countSinceDays(7));
        return "admin/inquiries";
    }

    @PostMapping("/dashboard/home")
    public String updateHome(
        @RequestParam("heroKicker") String heroKicker,
        @RequestParam("heroTitle") String heroTitle,
        @RequestParam("heroLead") String heroLead,
        @RequestParam("heroTopEyebrow") String heroTopEyebrow,
        @RequestParam("heroTopText") String heroTopText,
        @RequestParam("heroBottomEyebrow") String heroBottomEyebrow,
        @RequestParam("heroBottomText") String heroBottomText,
        RedirectAttributes redirectAttributes,
        HttpSession session
    ) {
        if (!isAuthenticated(session)) {
            return "redirect:/admin/login";
        }

        siteSettingService.saveAll(Map.of(
            "home.hero.kicker", clean(heroKicker),
            "home.hero.title", clean(heroTitle),
            "home.hero.lead", clean(heroLead),
            "home.hero.topEyebrow", clean(heroTopEyebrow),
            "home.hero.topText", clean(heroTopText),
            "home.hero.bottomEyebrow", clean(heroBottomEyebrow),
            "home.hero.bottomText", clean(heroBottomText)
        ));

        redirectAttributes.addFlashAttribute("homeSuccess", "Homepage content updated successfully.");
        return "redirect:/admin/home-content";
    }

    @PostMapping("/dashboard/about")
    public String updateAbout(
        @RequestParam("aboutHeroKicker") String aboutHeroKicker,
        @RequestParam("aboutHeroTitle") String aboutHeroTitle,
        @RequestParam("aboutHeroLead") String aboutHeroLead,
        @RequestParam("aboutSectionKicker") String aboutSectionKicker,
        @RequestParam("aboutSectionTitle") String aboutSectionTitle,
        @RequestParam("aboutPara1") String aboutPara1,
        @RequestParam("aboutPara2") String aboutPara2,
        @RequestParam("aboutMission") String aboutMission,
        @RequestParam("aboutVision") String aboutVision,
        RedirectAttributes redirectAttributes,
        HttpSession session
    ) {
        if (!isAuthenticated(session)) {
            return "redirect:/admin/login";
        }

        siteSettingService.saveAll(Map.of(
            "about.hero.kicker", clean(aboutHeroKicker),
            "about.hero.title", clean(aboutHeroTitle),
            "about.hero.lead", clean(aboutHeroLead),
            "about.section.kicker", clean(aboutSectionKicker),
            "about.section.title", clean(aboutSectionTitle),
            "about.section.para1", clean(aboutPara1),
            "about.section.para2", clean(aboutPara2),
            "about.mission", clean(aboutMission),
            "about.vision", clean(aboutVision)
        ));

        redirectAttributes.addFlashAttribute("aboutSuccess", "About page content updated successfully.");
        return "redirect:/admin/about-content";
    }

    @PostMapping("/dashboard/contact")
    public String updateContact(
        @RequestParam("contactHeroKicker") String contactHeroKicker,
        @RequestParam("contactHeroTitle") String contactHeroTitle,
        @RequestParam("contactHeroLead") String contactHeroLead,
        @RequestParam("contactSectionKicker") String contactSectionKicker,
        @RequestParam("contactSectionTitle") String contactSectionTitle,
        @RequestParam("contactSectionLead") String contactSectionLead,
        @RequestParam("contactEmail") String contactEmail,
        @RequestParam("contactPhone1") String contactPhone1,
        @RequestParam("contactPhone2") String contactPhone2,
        @RequestParam("contactLocation") String contactLocation,
        RedirectAttributes redirectAttributes,
        HttpSession session
    ) {
        if (!isAuthenticated(session)) {
            return "redirect:/admin/login";
        }

        siteSettingService.saveAll(Map.of(
            "contact.hero.kicker", clean(contactHeroKicker),
            "contact.hero.title", clean(contactHeroTitle),
            "contact.hero.lead", clean(contactHeroLead),
            "contact.section.kicker", clean(contactSectionKicker),
            "contact.section.title", clean(contactSectionTitle),
            "contact.section.lead", clean(contactSectionLead),
            "contact.email", clean(contactEmail),
            "contact.phone1", clean(contactPhone1),
            "contact.phone2", clean(contactPhone2),
            "contact.location", clean(contactLocation)
        ));

        redirectAttributes.addFlashAttribute("contactSuccess", "Contact details updated successfully.");
        return "redirect:/admin/contact";
    }

    @PostMapping("/dashboard/footer")
    public String updateFooter(
        @RequestParam("footerTagline") String footerTagline,
        @RequestParam("footerDescription") String footerDescription,
        @RequestParam("footerCompanyHeading") String footerCompanyHeading,
        @RequestParam("footerReachHeading") String footerReachHeading,
        @RequestParam("footerBottomText") String footerBottomText,
        RedirectAttributes redirectAttributes,
        HttpSession session
    ) {
        if (!isAuthenticated(session)) {
            return "redirect:/admin/login";
        }

        siteSettingService.saveAll(Map.of(
            "footer.tagline", clean(footerTagline),
            "footer.description", clean(footerDescription),
            "footer.companyHeading", clean(footerCompanyHeading),
            "footer.reachHeading", clean(footerReachHeading),
            "footer.bottomText", clean(footerBottomText)
        ));

        redirectAttributes.addFlashAttribute("footerSuccess", "Footer content updated successfully.");
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/dashboard/maintenance")
    public String updateMaintenanceMode(
        @RequestParam(name = "enabled", defaultValue = "false") boolean enabled,
        RedirectAttributes redirectAttributes,
        HttpSession session
    ) {
        if (!isAuthenticated(session)) {
            return "redirect:/admin/login";
        }

        siteSettingService.saveMaintenanceMode(enabled);
        redirectAttributes.addFlashAttribute(
            "maintenanceSuccess",
            enabled
                ? "Maintenance mode enabled. Public visitors will now see the maintenance page."
                : "Maintenance mode disabled. The public website is live again."
        );
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/dashboard/services/add")
    public String addService(
        @RequestParam("title") String title,
        @RequestParam("description") String description,
        @RequestParam("linkLabel") String linkLabel,
        @RequestParam("iconName") String iconName,
        RedirectAttributes redirectAttributes,
        HttpSession session
    ) {
        if (!isAuthenticated(session)) {
            return "redirect:/admin/login";
        }

        if (clean(title).isBlank() || clean(description).isBlank()) {
            redirectAttributes.addFlashAttribute("serviceError", "Please enter service title and description.");
            return "redirect:/admin/services";
        }

        ServiceItem item = new ServiceItem();
        item.setTitle(clean(title));
        item.setDescription(clean(description));
        item.setLinkLabel(clean(linkLabel).isBlank() ? "Learn more" : clean(linkLabel));
        item.setIconName(clean(iconName).isBlank() ? "software" : clean(iconName));
        item.setSortOrder(serviceItemService.findAll().size() + 1);
        serviceItemService.save(item);

        redirectAttributes.addFlashAttribute("serviceSuccess", "Service added successfully.");
        return "redirect:/admin/services";
    }

    @PostMapping("/dashboard/services/{id}")
    public String updateService(
        @PathVariable("id") Long id,
        @RequestParam("title") String title,
        @RequestParam("description") String description,
        @RequestParam("linkLabel") String linkLabel,
        @RequestParam("iconName") String iconName,
        @RequestParam("sortOrder") int sortOrder,
        RedirectAttributes redirectAttributes,
        HttpSession session
    ) {
        if (!isAuthenticated(session)) {
            return "redirect:/admin/login";
        }

        serviceItemService.findById(id).ifPresent(item -> {
            item.setTitle(clean(title));
            item.setDescription(clean(description));
            item.setLinkLabel(clean(linkLabel));
            item.setIconName(clean(iconName));
            item.setSortOrder(sortOrder);
            serviceItemService.save(item);
        });

        redirectAttributes.addFlashAttribute("serviceSuccess", "Service updated successfully.");
        return "redirect:/admin/services";
    }

    @PostMapping("/dashboard/services/{id}/delete")
    public String deleteService(
        @PathVariable("id") Long id,
        RedirectAttributes redirectAttributes,
        HttpSession session
    ) {
        if (!isAuthenticated(session)) {
            return "redirect:/admin/login";
        }

        serviceItemService.deleteById(id);
        redirectAttributes.addFlashAttribute("serviceSuccess", "Service deleted successfully.");
        return "redirect:/admin/services";
    }

    @PostMapping("/dashboard/portfolio/add")
    public String addPortfolio(
        @RequestParam("title") String title,
        @RequestParam("summary") String summary,
        @RequestParam("dateLabel") String dateLabel,
        @RequestParam("status") String status,
        @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
        RedirectAttributes redirectAttributes,
        HttpSession session
    ) {
        if (!isAuthenticated(session)) {
            return "redirect:/admin/login";
        }

        if (clean(title).isBlank() || clean(summary).isBlank()) {
            redirectAttributes.addFlashAttribute("portfolioError", "Please enter portfolio title and summary.");
            return "redirect:/admin/portfolio";
        }

        PortfolioItem item = new PortfolioItem();
        item.setTitle(clean(title));
        item.setSummary(clean(summary));
        item.setDateLabel(clean(dateLabel).isBlank() ? "Current" : clean(dateLabel));
        item.setStatus(clean(status).isBlank() ? "Published" : clean(status));
        String storedImagePath = storePortfolioImage(imageFile, DEFAULT_PORTFOLIO_IMAGE, redirectAttributes);
        if (storedImagePath == null) {
            return "redirect:/admin/portfolio";
        }
        item.setImagePath(storedImagePath);
        item.setSortOrder(portfolioItemService.findAll().size() + 1);
        portfolioItemService.save(item);

        redirectAttributes.addFlashAttribute("portfolioSuccess", "Portfolio item added successfully.");
        return "redirect:/admin/portfolio";
    }

    @PostMapping("/dashboard/portfolio/{id}")
    public String updatePortfolio(
        @PathVariable("id") Long id,
        @RequestParam("title") String title,
        @RequestParam("summary") String summary,
        @RequestParam("dateLabel") String dateLabel,
        @RequestParam("status") String status,
        @RequestParam("existingImagePath") String existingImagePath,
        @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
        @RequestParam("sortOrder") int sortOrder,
        RedirectAttributes redirectAttributes,
        HttpSession session
    ) {
        if (!isAuthenticated(session)) {
            return "redirect:/admin/login";
        }

        String storedImagePath = storePortfolioImage(imageFile, existingImagePath, redirectAttributes);
        if (storedImagePath == null) {
            return "redirect:/admin/portfolio";
        }

        portfolioItemService.findById(id).ifPresent(item -> {
            item.setTitle(clean(title));
            item.setSummary(clean(summary));
            item.setDateLabel(clean(dateLabel));
            item.setStatus(clean(status));
            item.setImagePath(storedImagePath);
            item.setSortOrder(sortOrder);
            portfolioItemService.save(item);
        });

        redirectAttributes.addFlashAttribute("portfolioSuccess", "Portfolio item updated successfully.");
        return "redirect:/admin/portfolio";
    }

    @PostMapping("/dashboard/portfolio/{id}/delete")
    public String deletePortfolio(
        @PathVariable("id") Long id,
        RedirectAttributes redirectAttributes,
        HttpSession session
    ) {
        if (!isAuthenticated(session)) {
            return "redirect:/admin/login";
        }

        portfolioItemService.deleteById(id);
        redirectAttributes.addFlashAttribute("portfolioSuccess", "Portfolio item deleted successfully.");
        return "redirect:/admin/portfolio";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/admin/login";
    }

    private boolean isAuthenticated(HttpSession session) {
        return Boolean.TRUE.equals(session.getAttribute(SESSION_ADMIN_AUTH));
    }

    private void populateAdminLayout(Model model, String activeNav, String pageTitle, String breadcrumb) {
        model.addAttribute("adminName", adminDisplayName);
        model.addAttribute("adminRole", "Website Administrator");
        model.addAttribute("pageTitle", pageTitle);
        model.addAttribute("pageBreadcrumb", breadcrumb);
        model.addAttribute("sidebarItems", buildSidebarItems(activeNav));
    }

    private List<AdminNavItem> buildSidebarItems(String activeNav) {
        long serviceCount = serviceItemService.findAll().size();
        long portfolioCount = portfolioItemService.findAll().size();
        long inquiryCount = contactInquiryService.countAll();
        int homeFieldCount = defaultHomeSettings().size();
        int aboutFieldCount = defaultAboutSettings().size();
        int contactFieldCount = defaultContactSettings().size();

        return List.of(
            new AdminNavItem("Dashboard", "/admin/dashboard", "D", NAV_DASHBOARD.equals(activeNav), "Live"),
            new AdminNavItem("Home Content", "/admin/home-content", "H", NAV_HOME.equals(activeNav), String.valueOf(homeFieldCount)),
            new AdminNavItem("About Content", "/admin/about-content", "A", NAV_ABOUT.equals(activeNav), String.valueOf(aboutFieldCount)),
            new AdminNavItem("Services", "/admin/services", "S", NAV_SERVICES.equals(activeNav), String.valueOf(serviceCount)),
            new AdminNavItem("Portfolio", "/admin/portfolio", "P", NAV_PORTFOLIO.equals(activeNav), String.valueOf(portfolioCount)),
            new AdminNavItem("Contact", "/admin/contact", "C", NAV_CONTACT.equals(activeNav), String.valueOf(contactFieldCount)),
            new AdminNavItem("Inquiries", "/admin/inquiries", "I", NAV_INQUIRIES.equals(activeNav), String.valueOf(inquiryCount)),
            new AdminNavItem("Logout", "/admin/logout", "L", false, null)
        );
    }

    private List<AdminSectionCard> buildManagementCards() {
        return List.of(
            new AdminSectionCard(
                "Home Content",
                "/admin/home-content",
                "Edit the homepage hero message and supporting copy.",
                String.valueOf(defaultHomeSettings().size()),
                "editable fields"
            ),
            new AdminSectionCard(
                "About Content",
                "/admin/about-content",
                "Update your company story, mission, and vision content.",
                String.valueOf(defaultAboutSettings().size()),
                "editable fields"
            ),
            new AdminSectionCard(
                "Services",
                "/admin/services",
                "Add, update, sort, and remove service cards shown on the website.",
                String.valueOf(serviceItemService.findAll().size()),
                "live service items"
            ),
            new AdminSectionCard(
                "Portfolio",
                "/admin/portfolio",
                "Manage portfolio projects and the media displayed on the public site.",
                String.valueOf(portfolioItemService.findAll().size()),
                "portfolio entries"
            ),
            new AdminSectionCard(
                "Contact Content",
                "/admin/contact",
                "Control the contact page text, email, phone numbers, and location details.",
                String.valueOf(defaultContactSettings().size()),
                "contact fields"
            ),
            new AdminSectionCard(
                "Inquiries",
                "/admin/inquiries",
                "Review leads submitted through the website contact form.",
                String.valueOf(contactInquiryService.countAll()),
                "stored inquiries"
            )
        );
    }

    private List<AdminStatCard> buildStatCards() {
        long inquiryCount = contactInquiryService.countAll();
        long recentInquiryCount = contactInquiryService.countSinceDays(7);
        long serviceCount = serviceItemService.findAll().size();
        long portfolioCount = portfolioItemService.findAll().size();

        return List.of(
            new AdminStatCard("Total Inquiries", String.valueOf(inquiryCount), "Stored project leads", "01", "tone-blue"),
            new AdminStatCard("This Week", String.valueOf(recentInquiryCount), "Fresh inquiries received", "02", "tone-cyan"),
            new AdminStatCard("Services Listed", String.valueOf(serviceCount), "Editable website services", "03", "tone-indigo"),
            new AdminStatCard("Portfolio Items", String.valueOf(portfolioCount), "Live website portfolio rows", "04", "tone-gold")
        );
    }

    private List<Map<String, String>> buildLoginMetrics() {
        return List.of(
            Map.of(
                "value", String.valueOf(buildManagementCards().size()),
                "label", "Editable website sections"
            ),
            Map.of(
                "value", String.valueOf(serviceItemService.findAll().size()),
                "label", "Live services available"
            ),
            Map.of(
                "value", String.valueOf(contactInquiryService.countAll()),
                "label", "Stored client inquiries"
            )
        );
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

    private List<String> availableServiceIcons() {
        return List.of("software", "web", "design", "media", "strategy");
    }

    private String clean(String value) {
        return value == null ? "" : value.trim();
    }

    private String storePortfolioImage(
        MultipartFile imageFile,
        String fallbackImagePath,
        RedirectAttributes redirectAttributes
    ) {
        String cleanFallback = clean(fallbackImagePath).isBlank() ? DEFAULT_PORTFOLIO_IMAGE : clean(fallbackImagePath);

        if (imageFile == null || imageFile.isEmpty()) {
            return cleanFallback;
        }

        String contentType = clean(imageFile.getContentType());
        if (!contentType.startsWith("image/")) {
            redirectAttributes.addFlashAttribute("portfolioError", "Please upload a valid image file.");
            return null;
        }

        String extension = extractFileExtension(imageFile.getOriginalFilename());
        String filename = "portfolio-" + UUID.randomUUID() + extension;

        try {
            Files.createDirectories(PORTFOLIO_UPLOAD_DIRECTORY);

            Path targetFile = PORTFOLIO_UPLOAD_DIRECTORY.resolve(filename).normalize();
            try (InputStream inputStream = imageFile.getInputStream()) {
                Files.copy(inputStream, targetFile, StandardCopyOption.REPLACE_EXISTING);
            }

            return "/uploads/portfolio/" + filename;
        } catch (IOException exception) {
            redirectAttributes.addFlashAttribute("portfolioError", "Unable to upload the selected image right now.");
            return null;
        }
    }

    private String extractFileExtension(String filename) {
        String cleanFilename = clean(filename);
        int extensionIndex = cleanFilename.lastIndexOf('.');

        if (extensionIndex < 0 || extensionIndex == cleanFilename.length() - 1) {
            return ".png";
        }

        return cleanFilename.substring(extensionIndex);
    }
}
