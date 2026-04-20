/**
 * RentMIS Login Handler
 */
'use strict';

document.addEventListener('DOMContentLoaded', function () {
  // Already logged in → redirect
  if (Auth.isLoggedIn()) {
    const user = Auth.getUser();
    window.location.href = Auth.getHomeByRole(user.role);
    return;
  }

  const form    = document.getElementById('loginForm');
  const errBox  = document.getElementById('loginError');
  const btn     = document.getElementById('loginBtn');
  const btnText = document.getElementById('loginBtnText');
  const spinner = document.getElementById('loginBtnSpinner');

  // Toggle password visibility
  document.querySelectorAll('.form-password-toggle .input-group-text').forEach(el => {
    el.addEventListener('click', () => {
      const inp = el.closest('.input-group').querySelector('input');
      const icon = el.querySelector('i');
      if (inp.type === 'password') {
        inp.type = 'text';
        icon.classList.replace('tabler-eye-off', 'tabler-eye');
      } else {
        inp.type = 'password';
        icon.classList.replace('tabler-eye', 'tabler-eye-off');
      }
    });
  });

  form && form.addEventListener('submit', async e => {
    e.preventDefault();
    const email    = (document.getElementById('email') || document.getElementById('username')).value.trim();
    const password = document.getElementById('password').value;

    if (!email || !password) { showErr('Please enter email and password.'); return; }

    setLoading(true); hideErr();

    const res = await API.login(email, password);

    if (res && res.ok && res.data && res.data.success) {
      const d = res.data.data;
      const jwt = d.token || d.accessToken;
      Auth.save(jwt, d.user);
      // Clear any session language override so new user starts with their profile language
      sessionStorage.removeItem('viewLangSession');
      btnText.textContent = 'Redirecting…';
      setTimeout(() => window.location.href = Auth.getHomeByRole(d.user.role), 300);
    } else {
      showErr((res && res.data && res.data.message) || 'Invalid credentials.');
      setLoading(false);
    }
  });

  function setLoading(on) {
    btn.disabled = on;
    btnText.textContent = on ? 'Signing in…' : 'Sign in';
    spinner && spinner.classList.toggle('d-none', !on);
  }
  function showErr(msg) { errBox.textContent = msg; errBox.classList.remove('d-none'); }
  function hideErr()    { errBox.classList.add('d-none'); }
});

function logout() {
  const token = Auth.getToken();
  fetch('/api/auth/logout', { method: 'POST', headers: { Authorization: 'Bearer ' + token } })
    .finally(() => { Auth.clear(); window.location.href = '/login'; });
}
window.logout = logout;
