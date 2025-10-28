<script>
  import { goto } from '$app/navigation';
  import { page } from '$app/stores';

  // Props (empty by default; you’ll wire real values later)
  export let role = '';        // 'admin' | 'doctor' | 'patient' (optional; auto-detects if empty)
  export let roleIcon = '';    // path to your icon (e.g. '/assets/icons/admin.png')
  export let name = '';        // user name (optional)
  export let email = '';       // user email (optional)

  // Auto-detect role from URL if not provided: adminView / doctorView / patientView
  $: detected = (() => {
    const p = $page?.url?.pathname?.toLowerCase() || '';
    if (p.includes('adminview')) return 'admin';
    if (p.includes('doctorview')) return 'doctor';
    if (p.includes('patientview')) return 'patient';
    return '';
  })();
  $: finalRole = (role || detected || '');

  // Initials (only shown if name provided)
  $: initials = (name || '')
    .split(' ')
    .filter(Boolean)
    .map(p => p[0])
    .join('')
    .slice(0, 2)
    .toUpperCase();

  function logout(){
    localStorage.removeItem('token');
    goto('/');
  }

  $: roleLabel = finalRole ? finalRole[0].toUpperCase() + finalRole.slice(1) : '';
</script>

<header class="topbar">
  <div class="brand">
    <span class="title">Healthcare Data Portal</span>

    {#if finalRole}
      <span class={`role role--${finalRole}`}>
        {#if roleIcon}
          <img src={roleIcon} alt="" class="icon" width="12" height="12" />
        {/if}
        {roleLabel}
      </span>
    {/if}
  </div>

  <div class="user">
    {#if initials}
      <div class="avatar" aria-hidden="true">{initials}</div>
    {/if}
    <div>
      <div class="name">{name || '—'}</div>
      <div class="muted email">{email || '—'}</div>
    </div>
    <button class="btn ghost" on:click={logout}>
      <img src="/src/lib/assets/pictures/exit.png" alt="" class="icon" width="12" height="12" />
      Logout
    </button>
  </div>
</header>

<style>
  :root{
    --ink:#0f172a; --muted:#6b7280; --ring:#e5e7eb;
    --pill-patient:#111827;   /* dark */
    --pill-doctor:#000000;    /* black */
    --pill-admin:#dc2626;     /* red */
  }
  .topbar{
    display:flex; align-items:center; justify-content:space-between;
    padding:16px 24px; border-bottom:1px solid var(--ring); background:#fff;
  }
  .brand{ display:flex; align-items:center; gap:12px; }
  .title{ font-weight:700; font-size:20px; color:var(--ink); }

  .role{
    display:inline-flex; align-items:center; gap:6px;
    padding:6px 10px; border-radius:999px; font-weight:700; font-size:12px;
    color:#fff; border:1px solid transparent;
  }
  .role .icon{ display:inline-block; }
  .role--patient{ background:var(--pill-patient); }
  .role--doctor { background:var(--pill-doctor); }
  .role--admin  { background:var(--pill-admin); }

  .user{ display:flex; align-items:center; gap:12px; }
  .avatar{
    width:40px; height:40px; border-radius:999px; background:#eef2ff;
    color:#374151; display:grid; place-items:center; font-weight:700;
    border:1px solid var(--ring);
  }
  .name{ font-weight:600; color:var(--ink); }
  .muted{ color:var(--muted); font-size:12px; }
  .btn.ghost{
    background:#fff; border:1px solid var(--ring); border-radius:10px;
    padding:8px 12px; display:inline-flex; align-items:center; gap:8px; font-weight:600;
  }
</style>
