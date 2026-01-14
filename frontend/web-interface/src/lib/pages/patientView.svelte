<script>
  import Header from '$lib/components/Header.svelte';
  import DashboardCard from '$lib/components/DashboardCard.svelte';
  import Tabs from '$lib/components/Tabs.svelte';
  import PersonalInfoForm from '$lib/components/PersonalInfoForm.svelte';
  import MedicalRecords from '$lib/pages/MedicalRecords.svelte';
  import PrivacyConsent from '$lib/pages/PrivacyConsent.svelte';
  import { onMount } from 'svelte';
  export let personalInfo = null;

  let activeTab = 'personal';
  let isEdit = false;



  const API_BASE =
    import.meta.env.VITE_API_BASE ?? 'http://localhost:8080/api';

  let totalRecords = 0;
  let loadingTotalRecords = false;

  function authFetch(url, options = {}) {
    const token = typeof localStorage !== 'undefined' ? localStorage.getItem('token') : null;
    const headers = new Headers(options.headers || {});
    if (token) headers.set('Authorization', `Bearer ${token}`);

    return fetch(url, { ...options, headers, credentials: 'include' });
  }

  async function loadTotalRecordsOnLogin() {
    loadingTotalRecords = true;
    try {
      const url = `${API_BASE.replace(/\/$/, '')}/measurements/me?limit=1000`;
      const res = await authFetch(url);

      if (!res.ok) {
        // wenn z.B. patient nicht eingeloggt / token ungültig
        totalRecords = 0;
        return;
      }

      const data = await res.json().catch(() => []);
      totalRecords = Array.isArray(data) ? data.length : 0;
    } catch {
      totalRecords = 0;
    } finally {
      loadingTotalRecords = false;
    }
  }

  onMount(async () => {
    await loadTotalRecordsOnLogin();
  });


  function handleMedicalCount(e) {
    totalRecords = e?.detail?.total ?? 0;
  }

  $: consentText =
    personalInfo?.consentGiven === true ? 'Consented'
    : personalInfo?.consentGiven === false ? 'No Consent'
    : 'Consented';



  const toggleEdit = () => (isEdit = !isEdit);
  async function handleSave(e) {
    const token = typeof localStorage !== 'undefined' ? localStorage.getItem('token') : null;
    if (!token) {
      alert('Not authenticated');
      isEdit = false;
      return;
    }

    const { form } = e.detail || {};
    const payload = {
      phoneNumber: form?.phone ?? '',
      streetAddress: form?.address?.street ?? '',
      city: form?.address?.city ?? '',
      state: form?.address?.state ?? '',
      zipCode: form?.address?.zip ?? '',
      country: form?.address?.country ?? '',
      emergencyContactName: form?.emergency?.name ?? '',
      emergencyContactPhone: form?.emergency?.phone ?? '',
      emergencyContactRelation: form?.emergency?.relation ?? ''
    };

    try {
      const res = await fetch('http://localhost:8080/api/personal-info', {
        method: 'PUT',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(payload)
      });

      let data = null;
      try { data = await res.json(); } catch {}

      if (res.ok && data) {
        // Update local view model so UI reflects saved values
        personalInfo = data;
        isEdit = false;
      } else {
        const msg = typeof data === 'string' ? data : (data?.message || 'Failed to save changes');
        alert(msg);
      }
    } catch (err) {
      alert('Network error while saving');
    }
  }
  const handleCancel = () => (isEdit = false);
  const handleExport = async () => {
    const token =
      typeof localStorage !== 'undefined' ? localStorage.getItem('token') : null;

    if (!token) {
      alert('Not authenticated');
      return;
    }

    try {
      const url = `http://localhost:8080/api/export/my/patient/pdf`;

      const res = await fetch(url, {
        method: 'GET',
        headers: {
          Authorization: `Bearer ${token}`
        }
      });

      if (!res.ok) {
        const msg = await res.text().catch(() => null);
        throw new Error(msg || `PDF export failed (${res.status})`);
      }

      const blob = await res.blob();

      // Try to use filename from Content-Disposition (fallback if not present)
      const cd = res.headers.get('Content-Disposition') || '';
      const match = cd.match(/filename="?([^"]+)"?/i);
      const filename = match?.[1] || 'my_patient_report.pdf';

      // Trigger download
      const blobUrl = URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = blobUrl;
      a.download = filename;
      document.body.appendChild(a);
      a.click();
      a.remove();
      URL.revokeObjectURL(blobUrl);
    } catch (err) {
      alert(err?.message || 'Failed to export PDF');
    }
  };

</script>

<!-- Header: patient pill (dark), white text; point to your patient icon -->
<Header role="patient" roleIcon="/src/lib/assets/pictures/white-user.png" />

<main class="container">
  <!-- Hero / Stats -->
  <section class="hero" aria-labelledby="dashboard-heading">
    <div style="display:flex; align-items:center; justify-content:space-between; gap:12px; flex-wrap: wrap;">
      <div>
        <h1 id="dashboard-heading" style="margin:0 0 6px 0;">My Health Dashboard</h1>
        <p class="muted" style="margin:0;">View and manage your personal health information</p>
      </div>
      <div style="display:flex; gap:8px;">
        <button class="btn ghost" aria-label="Export PDF" on:click={handleExport}>
          <img src="/src/lib/assets/pictures/file-export.png" alt="icon" class="icon" width="12" height="12" />
          Export PDF
        </button>
        <button class="btn primary" aria-label="Edit Profile" on:click={toggleEdit}>
          {isEdit ? 'Exit Edit' : 'Edit Profile'}
        </button>
      </div>
    </div>

      <div class="cards" role="list">
        <DashboardCard
          title="Total Records"
          description="Medical records on file"
          value={totalRecords}
        />
        <DashboardCard
          title="Data Consent"
          description="Data sharing consent status"
          value={consentText}
          format="text"
        />
      </div>

  </section>

  {#if isEdit}
    <div class="notice" role="status">
      <span>
        <img src="/src/lib/assets/pictures/bell.png" alt="icon" class="icon" width="12" height="12" />
        You are in edit mode. Make your changes and click <strong>“Save Changes”</strong> when finished.
      </span>
    </div>
  {/if}

  <!-- Tabs -->
  <nav class="tabs" aria-label="Sections">
    <Tabs
      bind:activeTab
      items={[
        { id: 'personal', label: 'Personal Information' },
        { id: 'medical', label: 'Medical Records' },
        { id: 'privacy', label: 'Privacy & Consent' }
      ]}
    />
  </nav>

  <!-- Tab panels -->
  {#if activeTab === 'personal'}
    <section class="panel" aria-labelledby="personal-info">
      <h2 id="personal-info" style="margin:0 0 8px 0;">Personal Information</h2>
        <p class="muted">You can edit your phone, address, and emergency contact details; core identity fields stay read-only.</p>
      <PersonalInfoForm editing={isEdit} {personalInfo} on:save={handleSave} on:cancel={handleCancel} />
    </section>
  {:else if activeTab === 'medical'}
    <MedicalRecords on:count={handleMedicalCount} />
  {:else}
    <PrivacyConsent />
  {/if}
</main>
