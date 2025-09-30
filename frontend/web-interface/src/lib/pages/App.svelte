<script>
  import Header from '../../../indexPage/src/components/Header.svelte';
  import DashboardCard from '../../../indexPage/src/components/DashboardCard.svelte';
  import Tabs from '../../../indexPage/src/components/Tabs.svelte';
  import PersonalInfoForm from '../../../indexPage/src/components/PersonalInfoForm.svelte';
  let activeTab = 'personal';
</script>

<Header />

<main class="container">
  <section class="hero" aria-labelledby="dashboard-heading">
    <div style="display:flex; align-items:center; justify-content:space-between; gap:12px; flex-wrap: wrap;">
      <div>
        <h1 id="dashboard-heading" style="margin:0 0 6px 0;">My Health Dashboard</h1>
        <p class="muted" style="margin:0;">View and manage your personal health information</p>
      </div>
      <div style="display:flex; gap:8px;">
        <button class="btn ghost" aria-label="Export PDF">📥 Export PDF</button>
        <button class="btn ghost" aria-label="Detailed View">👁️ Detailed View</button>
        <button class="btn primary" aria-label="Edit Profile">✏️ Edit Profile</button>
      </div>
    </div>

    <div class="cards" role="list">
      <DashboardCard title="Total Records" description="Medical records on file" />
      <DashboardCard title="Active Treatments" description="Ongoing medical care" />
      <DashboardCard title="Data Consent" description="Data sharing consent status" format="text" />
    </div>
  </section>

  <nav class="tabs" aria-label="Sections">
    <Tabs bind:activeTab items={[
      { id: 'personal', label: 'Personal Information' },
      { id: 'medical', label: 'Medical Records' },
      { id: 'privacy', label: 'Privacy & Consent' }
    ]} />
  </nav>

  {#if activeTab === 'personal'}
    <section class="panel" aria-labelledby="personal-info">
      <h2 id="personal-info" style="margin:0 0 8px 0;">Personal Information</h2>
      <p class="muted">View and update your personal contact details and address</p>
      <PersonalInfoForm />
    </section>
  {:else if activeTab === 'medical'}
    <section class="panel">
      <h2>Medical Records</h2>
      <p class="muted">This section will display medical records from the API once connected.</p>
    </section>
  {:else}
    <section class="panel">
      <h2>Privacy & Consent</h2>
      <p class="muted">Manage data sharing preferences. Hook this view to your API.</p>
    </section>
  {/if}
</main>
