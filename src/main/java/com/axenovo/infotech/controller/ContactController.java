package com.axenovo.infotech.controller;

import com.axenovo.infotech.model.ContactInquiryForm;
import com.axenovo.infotech.service.ContactInquiryService;
import com.axenovo.infotech.service.SiteSettingService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

@Controller
public class ContactController {

    private static final Pattern EMAIL_PATTERN =
        Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}$", Pattern.CASE_INSENSITIVE);

    private static final Pattern PHONE_PATTERN =
        Pattern.compile("^[0-9+()\\-\\s]{7,20}$");

    private final ContactInquiryService contactInquiryService;
    private final SiteSettingService siteSettingService;

    public ContactController(ContactInquiryService contactInquiryService, SiteSettingService siteSettingService) {
        this.contactInquiryService = contactInquiryService;
        this.siteSettingService = siteSettingService;
    }

    @GetMapping("/contact")
    public String contact(Model model) {
        ensureForm(model);
        addContactContent(model);
        return "contact";
    }

    @PostMapping("/contact")
    public String submitContact(
        @ModelAttribute("contactForm") ContactInquiryForm contactForm,
        Model model,
        RedirectAttributes redirectAttributes
    ) {
        normalize(contactForm);
        String validationMessage = validate(contactForm);

        if (validationMessage != null) {
            addContactContent(model);
            model.addAttribute("formError", validationMessage);
            return "contact";
        }

        contactInquiryService.save(contactForm);
        redirectAttributes.addFlashAttribute("successMessage", "Thanks, your project inquiry has been saved successfully.");
        redirectAttributes.addFlashAttribute("contactForm", new ContactInquiryForm());
        return "redirect:/contact#contact";
    }

    private void ensureForm(Model model) {
        if (!model.containsAttribute("contactForm")) {
            model.addAttribute("contactForm", new ContactInquiryForm());
        }
    }

    private void addContactContent(Model model) {
        model.addAttribute("contactContent", siteSettingService.getMany(defaultContactSettings()));
        model.addAttribute("footerContent", siteSettingService.getMany(defaultFooterSettings()));
    }

    private void normalize(ContactInquiryForm form) {
        form.setName(clean(form.getName()));
        form.setEmail(clean(form.getEmail()));
        form.setPhone(clean(form.getPhone()));
        form.setProjectBrief(clean(form.getProjectBrief()));
    }

    private String validate(ContactInquiryForm form) {
        if (form.getName().isBlank()) {
            return "Please enter your name.";
        }
        if (form.getEmail().isBlank() || !EMAIL_PATTERN.matcher(form.getEmail()).matches()) {
            return "Please enter a valid email address.";
        }
        if (form.getPhone().isBlank() || !PHONE_PATTERN.matcher(form.getPhone()).matches()) {
            return "Please enter a valid phone number.";
        }
        if (form.getProjectBrief().isBlank()) {
            return "Please share a short project brief.";
        }
        if (form.getProjectBrief().length() < 20) {
            return "Project brief should be at least 20 characters so we can understand your requirement.";
        }
        return null;
    }

    private String clean(String value) {
        return value == null ? "" : value.trim();
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
