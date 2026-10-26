const API = '/api';

function getUser() {
  const data = sessionStorage.getItem('user');
  return data ? JSON.parse(data) : null;
}

function requireAuth() {
  if (!getUser()) {
    window.location.href = '/login.html';
    return null;
  }
  return getUser();
}

function logout() {
  sessionStorage.removeItem('user');
  window.location.href = '/login.html';
}

async function apiPost(url, body) {
  const res = await fetch(API + url, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(body)
  });
  const data = await res.json();
  if (!res.ok) throw new Error(data.error || 'Request failed');
  return data;
}

async function apiGet(url) {
  const res = await fetch(API + url);
  const data = await res.json();
  if (!res.ok) throw new Error(data.error || 'Request failed');
  return data;
}

async function apiPut(url, body) {
  const res = await fetch(API + url, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(body)
  });
  const data = await res.json();
  if (!res.ok) throw new Error(data.error || 'Request failed');
  return data;
}

async function apiDelete(url) {
  const res = await fetch(API + url, { method: 'DELETE' });
  const data = await res.json();
  if (!res.ok) throw new Error(data.error || 'Request failed');
  return data;
}

function formatCurrency(amount) {
  return 'Rs. ' + Number(amount).toFixed(2);
}

function escapeHtml(str) {
  if (str === null || str === undefined) return '';
  return String(str)
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&#39;');
}

function renderHeader(title, subtitle) {
  const user = getUser();
  const currentPath = window.location.pathname;
  
  // Render structure
  const html = `
    <header class="header">
      <a href="/dashboard.html" class="header-brand">
        <img id="navHeaderLogo" src="/images/logo.png" alt="Logo" onerror="this.src='/images/logo.png';">
        <div>
          <h1 id="navHeaderTitle">${title || 'Supermarket POS'}</h1>
          ${subtitle ? `<div class="subtitle">${subtitle}</div>` : ''}
        </div>
      </a>
      <nav class="header-nav">
        <a href="/dashboard.html" class="${currentPath.includes('dashboard') ? 'active' : ''}">Dashboard</a>
        <a href="/billing.html" class="${currentPath.includes('billing') ? 'active' : ''}">POS Billing</a>
        <a href="/ai-insights.html" class="${currentPath.includes('ai-insights') ? 'active' : ''}" style="background: linear-gradient(135deg, #7c3aed, #4f46e5); color: white; border-radius: 6px; padding: 6px 12px; font-weight: 700; box-shadow: 0 2px 6px rgba(124,58,237,0.3);">🤖 AI Intelligence</a>
        <a href="/monthly-income.html" class="${currentPath.includes('monthly-income') ? 'active' : ''}">Monthly Statement</a>
        <a href="/products.html" class="${currentPath.includes('products') ? 'active' : ''}">Products</a>
        <a href="/customers.html" class="${currentPath.includes('customers') ? 'active' : ''}">Customers</a>
        <a href="/credentials.html" class="${currentPath.includes('credentials') ? 'active' : ''}">Credentials</a>
        <a href="/shop.html" class="${currentPath.includes('shop') ? 'active' : ''}">Shop Profile</a>
        ${user ? `<span class="user-info" style="margin-left: 8px;">(${user.fullName})</span>` : ''}
        <button onclick="logout()" class="btn btn-outline-white" style="padding: 6px 12px; font-size: 0.85rem; margin-left: 6px;">Logout</button>
      </nav>
    </header>`;
    
  // Async update shop details if available
  setTimeout(async () => {
    try {
      const res = await fetch('/api/shop');
      if (res.ok) {
        const data = await res.json();
        const logo = document.getElementById('navHeaderLogo');
        const titleEl = document.getElementById('navHeaderTitle');
        if (data.shopName && titleEl && (!title || title === 'Supermarket POS')) {
          titleEl.textContent = data.shopName;
        }
        if (data.imagePath && logo) {
          logo.src = data.imagePath + '?t=' + Date.now();
        }
      }
    } catch (e) {}
  }, 50);

  return html;
}
