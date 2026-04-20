/**
 * RentMIS API Client
 * All calls go to the RentMIS Spring Boot backend at /api
 */
'use strict';

const API_BASE = '/api';

const Auth = {
  getToken: () => localStorage.getItem('rentmis_token'),
  getUser:  () => { try { return JSON.parse(localStorage.getItem('rentmis_user') || '{}'); } catch { return {}; } },
  isLoggedIn: () => !!localStorage.getItem('rentmis_token'),
  save: (token, user) => {
    localStorage.setItem('rentmis_token', token);
    localStorage.setItem('rentmis_user', JSON.stringify(user));
  },
  clear: () => {
    localStorage.removeItem('rentmis_token');
    localStorage.removeItem('rentmis_user');
  },
  redirectIfNotLoggedIn: () => {
    if (!Auth.isLoggedIn()) { window.location.href = '/login'; return true; }
    return false;
  },
  getHomeByRole: (role) => {
    switch (role) {
      case 'TENANT':   return '/html/tenant/dashboard.html';
      case 'LANDLORD': return '/html/landlord/dashboard.html';
      case 'AGENT':    return '/html/agent/dashboard.html';
      default:         return '/dashboard';
    }
  }
};

async function request(method, path, body = null) {
  const headers = { 'Content-Type': 'application/json' };
  const token = Auth.getToken();
  if (token) headers['Authorization'] = `Bearer ${token}`;

  const opts = { method, headers };
  if (body) opts.body = JSON.stringify(body);

  try {
    const res = await fetch(API_BASE + path, opts);
    if (res.status === 401) { Auth.clear(); window.location.href = '/login'; return null; }
    const data = await res.json();
    return { ok: res.ok, status: res.status, data };
  } catch (e) {
    return { ok: false, status: 0, data: { success: false, message: 'Network error' } };
  }
}

const API = {
  // Auth
  login:    (email, password) => request('POST', '/auth/login',    { username: email, password }),
  register: (body)            => request('POST', '/auth/register', body),
  logout:   ()                => request('POST', '/auth/logout'),
  me:       ()                => request('GET',  '/auth/me'),
  refresh:  (rt)              => request('POST', '/auth/refresh',  { refreshToken: rt }),

  // Users
  getUsers:           (params={}) => request('GET', '/users?' + new URLSearchParams(params)),
  getUser:            (id)        => request('GET',  `/users/${id}`),
  toggleUser:         (id)        => request('PUT',  `/users/${id}/toggle-active`),
  toggleUserActive:   (id)        => request('PUT',  `/users/${id}/toggle-active`),
  getProfile:         ()          => request('GET',  '/auth/me'),
  updateProfile:      (body)      => request('PUT',  '/users/me/profile', body),
  changePassword:     (body)      => request('PUT',  '/users/me/password', body),
  getAvailableTenants:()          => request('GET',  '/users/available-tenants'),
  reportTenant:       (id, reason)=> request('POST', `/users/${id}/report`, { reason }),
  getTenantReports:   (id)        => request('GET',  `/users/${id}/reports`),
  getReports:         (params={}) => request('GET',  '/users/reports?' + new URLSearchParams(params)),
  reviewReport:       (id, decision, notes) => request('PUT', `/users/reports/${id}/review`, { decision, notes }),

  // Properties
  getProperties: (params={})=> request('GET',  '/properties?' + new URLSearchParams(params)),
  getProperty:   (id)       => request('GET',  `/properties/${id}`),
  createProperty:(body)     => request('POST', '/properties', body),
  updateProperty:(id, body) => request('PUT',  `/properties/${id}`, body),
  deleteProperty:(id)       => request('DELETE',`/properties/${id}`),

  // Units
  getUnits:          (params={}) => request('GET',  '/units?' + new URLSearchParams(params)),
  getUnit:           (id)        => request('GET',  `/units/${id}`),
  getAvailableUnits: ()          => request('GET',  '/units/available'),
  getMyUnit:         ()          => request('GET',  '/units/my-unit'),
  createUnit:        (body)      => request('POST', '/units', body),
  updateUnit:        (id, body)  => request('PUT',  `/units/${id}`, body),

  // Contracts
  getContracts:     (params={})   => request('GET',  '/contracts?' + new URLSearchParams(params)),
  getContract:      (id)          => request('GET',  `/contracts/${id}`),
  createContract:   (body)        => request('POST', '/contracts', body),
  signLandlord:     (id, password) => request('POST', `/contracts/${id}/sign/landlord`, { password }),
  signTenant:       (id, password) => request('POST', `/contracts/${id}/sign/tenant`,   { password }),
  signContract:     (id, password) => request('POST', `/contracts/${id}/sign/tenant`,   { password }),
  terminateContract:(id, reason)  => request('POST', `/contracts/${id}/terminate`, { reason }),
  verifyContract:   (id)          => request('GET',  `/contracts/${id}/verify`),
  getContractPdfUrl:(id)          => `${API_BASE}/contracts/${id}/pdf`,
  openContractPdf: async (id) => {
    const token = Auth.getToken();
    const res   = await fetch(`${API_BASE}/contracts/${id}/pdf`, {
      headers: { 'Authorization': `Bearer ${token}` }
    });
    if (!res.ok) { alert('Failed to load PDF — ' + res.status); return; }
    const blob = await res.blob();
    const url  = URL.createObjectURL(blob);
    window.open(url, '_blank');
  },

  // Payments
  getPayments:              (params={}) => request('GET',  '/payments?' + new URLSearchParams(params)),
  getPayment:               (id)        => request('GET',  `/payments/${id}`),
  getPaymentByRef:          (ref)       => request('GET',  `/payments/by-ref/${ref}`),
  initiatePayment:          (body)      => request('POST', '/payments', body),
  initiateMultiPayment:     (body)      => request('POST', '/payments/bulk', body),
  initiateManualPayment:    (body)      => request('POST', '/payments/manual', body),
  recordCashPayment:        (body)      => request('POST', '/payments/cash', body),
  // body: { unitId, periods:[{month,year}], amountPerMonth, notes }
  getPendingConfirmations:  ()          => request('GET',  '/payments/pending-confirmation'),
  confirmPayment:           (id, note, password) => request('PUT', `/payments/${id}/confirm`, { note, password }),
  rejectPayment:            (id, reason)=> request('PUT',  `/payments/${id}/reject`,   { reason }),
  verifyPayment:            (txId)      => request('POST', `/payments/verify/${txId}`),
  verifyPaymentByRef:       (ref)       => request('POST', `/payments/verify-by-ref/${ref}`),
  getReceiptUrl:            (id)        => `${API_BASE}/payments/${id}/receipt-file`,
  uploadReceipt: async (id, file) => {
    const formData = new FormData();
    formData.append('file', file);
    const token = Auth.getToken();
    try {
      const res = await fetch(`${API_BASE}/payments/${id}/receipt`, {
        method: 'POST',
        headers: token ? { 'Authorization': `Bearer ${token}` } : {},
        body: formData,
      });
      const data = await res.json().catch(() => ({ success: false, message: 'Invalid server response' }));
      return { ok: res.ok, status: res.status, data };
    } catch (e) {
      return { ok: false, status: 0, data: { success: false, message: 'Network error during upload' } };
    }
  },

  // Invoices
  getInvoices:        (params={}) => request('GET',  '/invoices?' + new URLSearchParams(params)),
  getInvoice:         (id)        => request('GET',  `/invoices/${id}`),
  getInvoiceStats:    ()          => request('GET',  '/invoices/stats'),
  createInvoice:      (paymentId) => request('POST', `/invoices/for-payment/${paymentId}`),
  retryEbm:           (id)        => request('POST', `/invoices/${id}/retry-ebm`),
  generateEbm:        (id)        => request('POST', `/invoices/${id}/generate-ebm`),

  // Reports
  getDashboard: () => request('GET', '/reports/dashboard'),
};

window.API  = API;
window.Auth = Auth;
