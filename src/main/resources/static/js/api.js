/**
 * SnakeGuard - API Client (Vanilla JavaScript)
 * Handles CSRF tokens, session state, and communications with Spring Boot backend.
 */

const Api = {
  /**
   * Reads a cookie value by name.
   */
  getCookie(name) {
    const value = `; ${document.cookie}`;
    const parts = value.split(`; ${name}=`);
    if (parts.length === 2) {
      return decodeURIComponent(parts.pop().split(';').shift());
    }
    return null;
  },

  /**
   * Fetches CSRF token information from backend.
   */
  async getCsrfToken() {
    try {
      const response = await fetch('/api/auth/csrf', {
        method: 'GET',
        headers: { 'Accept': 'application/json' }
      });
      if (response.ok) {
        return await response.json();
      }
    } catch (e) {
      console.warn('Could not fetch CSRF token endpoint:', e);
    }
    return null;
  },

  /**
   * Helper to perform JSON requests with proper CSRF headers.
   */
  async request(url, options = {}) {
    const headers = {
      'Content-Type': 'application/json',
      'Accept': 'application/json',
      ...(options.headers || {})
    };

    // Attach CSRF token on mutating requests if present in cookie
    const isMutation = ['POST', 'PUT', 'DELETE', 'PATCH'].includes((options.method || 'GET').toUpperCase());
    if (isMutation) {
      const xsrfToken = this.getCookie('XSRF-TOKEN');
      if (xsrfToken) {
        headers['X-XSRF-TOKEN'] = xsrfToken;
      }
    }

    const config = {
      ...options,
      headers,
      credentials: 'same-origin' // Ensures session cookies are transmitted
    };

    const response = await fetch(url, config);
    const data = await response.json().catch(() => null);

    return {
      ok: response.ok,
      status: response.status,
      data
    };
  },

  /**
   * Registers a new user.
   */
  async register(userData) {
    return this.request('/api/auth/register', {
      method: 'POST',
      body: JSON.stringify(userData)
    });
  },

  /**
   * Authenticates user and initiates session.
   */
  async login(credentials) {
    return this.request('/api/auth/login', {
      method: 'POST',
      body: JSON.stringify(credentials)
    });
  },

  /**
   * Gets details of the currently authenticated user.
   */
  async getCurrentUser() {
    return this.request('/api/auth/me', {
      method: 'GET'
    });
  },

  /**
   * Terminates active session.
   */
  async logout() {
    return this.request('/api/auth/logout', {
      method: 'POST'
    });
  }
};
