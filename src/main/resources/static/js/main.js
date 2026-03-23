document.documentElement.classList.add("js");

const body = document.body;
const navToggle = document.querySelector("[data-nav-toggle]");
const siteNav = document.querySelector("[data-site-nav]");
const navLinks = siteNav ? siteNav.querySelectorAll("a") : [];
const yearNode = document.querySelector("[data-current-year]");
const contactForms = document.querySelectorAll("[data-contact-form]");

if (yearNode) {
  yearNode.textContent = String(new Date().getFullYear());
}

if (navToggle) {
  navToggle.addEventListener("click", () => {
    const isOpen = body.classList.toggle("nav-open");
    navToggle.setAttribute("aria-expanded", String(isOpen));
  });
}

navLinks.forEach((link) => {
  link.addEventListener("click", () => {
    body.classList.remove("nav-open");
    if (navToggle) {
      navToggle.setAttribute("aria-expanded", "false");
    }
  });
});

const revealItems = document.querySelectorAll("[data-reveal]");

revealItems.forEach((item) => {
  const delay = Number(item.dataset.revealDelay || 0);
  item.style.setProperty("--reveal-delay", `${delay}ms`);
});

if ("IntersectionObserver" in window) {
  const revealObserver = new IntersectionObserver(
    (entries, observer) => {
      entries.forEach((entry) => {
        if (!entry.isIntersecting) return;
        entry.target.classList.add("is-visible");
        observer.unobserve(entry.target);
      });
    },
    {
      threshold: 0.12,
      rootMargin: "0px 0px -5% 0px"
    }
  );

  revealItems.forEach((item) => revealObserver.observe(item));
} else {
  revealItems.forEach((item) => item.classList.add("is-visible"));
}

contactForms.forEach((contactForm) => {
  const formStatus = contactForm.querySelector("[data-form-status]");
  const submitButton = contactForm.querySelector('button[type="submit"]');

  contactForm.addEventListener("submit", () => {
    if (!contactForm.reportValidity()) {
      if (formStatus) {
        formStatus.textContent = "Please complete all required details before submitting your inquiry.";
      }
      return;
    }

    if (formStatus) {
      formStatus.textContent = "Sending your inquiry securely...";
      formStatus.classList.add("is-pending");
    }

    if (submitButton) {
      submitButton.disabled = true;
      submitButton.textContent = "Saving...";
    }
  });
});
const menuBtn = document.querySelector(".nav-toggle");

menuBtn.addEventListener("click", () => {
  menuBtn.classList.toggle("active");
});
