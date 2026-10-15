/**
 * SnakeGuard - Form Validation Helpers
 * Provides client-side validation logic and inline error rendering.
 */

const Validation = {
  isValidEmail(email) {
    if (!email) return false;
    const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return re.test(email.trim());
  },

  isValidPhone(phone) {
    if (!phone) return false;
    const re = /^[+0-9\s()-]{7,20}$/;
    return re.test(phone.trim());
  },

  isValidPassword(password) {
    // Minimum 8 characters
    return password && password.length >= 8;
  },

  showError(inputEl, errorEl, message) {
    if (inputEl) {
      inputEl.classList.add('input-error');
    }
    if (errorEl) {
      errorEl.textContent = message;
      errorEl.classList.add('active');
    }
  },

  clearError(inputEl, errorEl) {
    if (inputEl) {
      inputEl.classList.remove('input-error');
    }
    if (errorEl) {
      errorEl.textContent = '';
      errorEl.classList.remove('active');
    }
  },

  clearAll(form) {
    const errorInputs = form.querySelectorAll('.input-error');
    errorInputs.forEach(el => el.classList.remove('input-error'));

    const errorTexts = form.querySelectorAll('.field-error-text');
    errorTexts.forEach(el => {
      el.textContent = '';
      el.classList.remove('active');
    });
  }
};
