/**
 * SERPENT. — Authentication Logic (Login & Registration)
 * Connects the UI to Spring Boot / MySQL backend.
 */

document.addEventListener('DOMContentLoaded', () => {
  initEyeToggles();
  initSignupFlow();
  initLoginForm();
});

const EYE_OPEN_SVG = `<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="#6B7280" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"></path><circle cx="12" cy="12" r="3"></circle></svg>`;
const EYE_CLOSED_SVG = `<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="#007A78" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24"></path><line x1="1" y1="1" x2="23" y2="23"></line></svg>`;

/**
 * Password Visibility Toggle (Matches Eye Icon in Images 1 & 2)
 */
function initEyeToggles() {
  const toggleButtons = document.querySelectorAll('.eye-toggle-btn');
  toggleButtons.forEach(btn => {
    btn.addEventListener('click', () => {
      const targetId = btn.getAttribute('data-target');
      const input = document.getElementById(targetId);
      if (input) {
        if (input.type === 'password') {
          input.type = 'text';
          btn.innerHTML = EYE_CLOSED_SVG;
        } else {
          input.type = 'password';
          btn.innerHTML = EYE_OPEN_SVG;
        }
      }
    });
  });
}

/**
 * Sign-Up Flow (Role Selection + Registration Form)
 */
function initSignupFlow() {
  const formSection = document.getElementById('user-registration-section');
  const userRoleBtn = document.getElementById('btn-select-user-role');
  const roleCards = document.querySelectorAll('.role-card-serpent');

  roleCards.forEach(card => {
    card.addEventListener('click', (e) => {
      // If user clicked inside a button, let the button handler manage it
      if (e.target.tagName.toLowerCase() === 'button') return;
      roleCards.forEach(c => c.classList.remove('active-role'));
      card.classList.add('active-role');
      if (card.id === 'role-card-user' && formSection) {
        formSection.scrollIntoView({ behavior: 'smooth' });
        const firstInput = document.getElementById('fullName');
        if (firstInput) setTimeout(() => firstInput.focus(), 300);
      }
    });
  });

  if (userRoleBtn && formSection) {
    userRoleBtn.addEventListener('click', (e) => {
      e.stopPropagation();
      roleCards.forEach(c => c.classList.remove('active-role'));
      const userCard = document.getElementById('role-card-user');
      if (userCard) userCard.classList.add('active-role');
      formSection.scrollIntoView({ behavior: 'smooth' });
      const firstInput = document.getElementById('fullName');
      if (firstInput) setTimeout(() => firstInput.focus(), 300);
    });
  }

  const form = document.getElementById('signup-form');
  if (!form) return;

  const alertBox = document.getElementById('signup-alert');
  const submitBtn = document.getElementById('signup-submit-btn');

  form.addEventListener('submit', async (e) => {
    e.preventDefault();
    hideAlert(alertBox);
    clearInlineErrors(form);

    const fullName = (document.getElementById('fullName')?.value || '').trim();
    const email = (document.getElementById('email')?.value || '').trim();
    const phone = (document.getElementById('phone')?.value || '').trim();
    const country = (document.getElementById('country')?.value || '').trim();
    const password = document.getElementById('password')?.value || '';
    const confirmPassword = document.getElementById('confirmPassword')?.value || '';

    let hasErrors = false;

    if (!fullName || fullName.length < 2) {
      showInlineError('fullName', 'Please enter your full name (at least 2 characters).');
      hasErrors = true;
    }

    if (!Validation.isValidEmail(email)) {
      showInlineError('email', 'Please enter a valid email address.');
      hasErrors = true;
    }

    if (!Validation.isValidPhone(phone)) {
      showInlineError('phone', 'Please enter a valid phone number (7-20 digits).');
      hasErrors = true;
    }

    if (!country) {
      showInlineError('country', 'Please select your country.');
      hasErrors = true;
    }

    if (!Validation.isValidPassword(password)) {
      showInlineError('password', 'Password must be at least 8 characters long.');
      hasErrors = true;
    }

    if (password !== confirmPassword) {
      showInlineError('confirmPassword', 'Passwords do not match.');
      hasErrors = true;
    }

    if (hasErrors) return;

    setBtnLoading(submitBtn, true, 'Submitting registration...');

    try {
      const res = await Api.register({
        fullName,
        email,
        phone,
        country,
        password,
        confirmPassword
      });

      if (res.ok && res.data && res.data.success) {
        showAlert(alertBox, 'success', 'Registration successful! Redirecting to login...');
        form.reset();
        setTimeout(() => {
          window.location.href = 'login.html';
        }, 1200);
      } else {
        const msg = res.data?.message || 'Registration failed. Please check inputs.';
        showAlert(alertBox, 'error', msg);

        if (res.data?.error && typeof res.data.error === 'object') {
          for (const [f, m] of Object.entries(res.data.error)) {
            showInlineError(f, m);
          }
        }
      }
    } catch (err) {
      showAlert(alertBox, 'error', 'Server unreachable. Please verify Spring Boot is running.');
    } finally {
      setBtnLoading(submitBtn, false, 'Submit demo registration →');
    }
  });
}

/**
 * Login Flow (Matches Image 1)
 */
function initLoginForm() {
  const form = document.getElementById('login-form');
  if (!form) return;

  const alertBox = document.getElementById('login-alert');
  const submitBtn = document.getElementById('login-submit-btn');

  form.addEventListener('submit', async (e) => {
    e.preventDefault();
    hideAlert(alertBox);
    clearInlineErrors(form);

    const email = (document.getElementById('login-email')?.value || '').trim();
    const password = document.getElementById('login-password')?.value || '';

    let hasErrors = false;

    if (!email) {
      showInlineError('login-email', 'Please enter your email address.');
      hasErrors = true;
    }

    if (!password) {
      showInlineError('login-password', 'Please enter your password.');
      hasErrors = true;
    }

    if (hasErrors) return;

    setBtnLoading(submitBtn, true, 'Signing in...');

    try {
      const res = await Api.login({ email, password });

      if (res.ok && res.data && res.data.success) {
        showAlert(alertBox, 'success', `Welcome back, ${res.data.data.fullName}! Redirecting...`);
        setTimeout(() => {
          window.location.href = 'index.html';
        }, 800);
      } else {
        const msg = res.data?.message || 'Invalid email or password.';
        showAlert(alertBox, 'error', msg);
      }
    } catch (err) {
      showAlert(alertBox, 'error', 'Unable to reach the server. Please check connection.');
    } finally {
      setBtnLoading(submitBtn, false, 'Log In — Demo →');
    }
  });
}

function showInlineError(fieldId, msg) {
  const errEl = document.getElementById(`${fieldId}-error`);
  const inputEl = document.getElementById(fieldId);
  if (errEl) {
    errEl.textContent = msg;
    errEl.classList.add('active');
  }
  if (inputEl) {
    inputEl.style.borderColor = 'var(--color-emergency)';
  }
}

function clearInlineErrors(form) {
  const errors = form.querySelectorAll('.field-inline-error');
  errors.forEach(el => {
    el.textContent = '';
    el.classList.remove('active');
  });

  const inputs = form.querySelectorAll('.field-input, .field-select');
  inputs.forEach(el => {
    el.style.borderColor = '';
  });
}

function showAlert(alertEl, type, msg) {
  if (!alertEl) return;
  alertEl.className = `auth-status-alert ${type} active`;
  alertEl.textContent = msg;
}

function hideAlert(alertEl) {
  if (!alertEl) return;
  alertEl.className = 'auth-status-alert';
  alertEl.textContent = '';
}

function setBtnLoading(btn, isLoading, text) {
  if (!btn) return;
  btn.disabled = isLoading;
  btn.textContent = text;
}
