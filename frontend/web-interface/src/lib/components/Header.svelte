<script>
    import { goto } from '$app/navigation';
  // Static UI only — values are placeholders and can be wired to data later.
  export let name = '';
  export let email = '';
  $: initials = (name || ' ').split(' ').map(p=>p[0]).join('').slice(0,2).toUpperCase() || 'NA';
    function logout() {
    localStorage.removeItem('token');
    goto('/');
  }
</script>
<header class="topbar" role="banner">
  <div class="brand">
    <span>Healthcare Data Portal</span>
    <span class="role"><img src="~src/lib/assets/pictures/stats.png" alt="icon" width="20" height="20"> Patient</span>
  </div>
  <div class="user">
    <div class="avatar" aria-hidden="true">{initials}</div>
    <div>
      <div style="font-weight:600;">{name || '—'}</div>
      <div class="muted" style="font-size:12px;">{email || '—'}</div>
    </div>
    <button class="btn ghost" on:click={logout}>⎋ Logout</button>
  </div>
</header>
