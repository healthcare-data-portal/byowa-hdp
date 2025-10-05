<script>
  import Header from '$lib/components/Header.svelte';
  import DashboardCard from '$lib/components/DashboardCard.svelte';
  import Tabs from '$lib/components/Tabs.svelte';
  import PersonalInfoForm from '$lib/components/PersonalInfoForm.svelte';
  import MedicalRecords from '$lib/pages/MedicalRecords.svelte';
  import PrivacyConsent from '$lib/pages/PrivacyConsent.svelte';

  let activeTab = 'patients';
  let isEdit = false;

  const toggleEdit = () => (isEdit = !isEdit);
  const handleSave = () => (isEdit = false);
  const handleCancel = () => (isEdit = false);
</script>

<Header />

<main class="container">
  <!-- Hero -->
  <section class="hero" aria-labelledby="doctor-dashboard">
    <div class="row-between">
      <div>
        <h1 id="doctor-dashboard" style="margin:0 0 6px 0;">Doctor Dashboard</h1>
        <p class="muted" style="margin:0;">Manage your patients, records, and appointments</p>
      </div>
      <div class="actions-row">
        <button class="btn ghost" aria-label="Export CSV">📤 Export CSV</button>
        <button class="btn ghost" aria-label="Analytics">📊 Analytics</button>
        <button class="btn primary" on:click={toggleEdit}>
          {isEdit ? 'Exit Edit' : 'Edit Mode'}
        </button>
      </div>
    </div>

    <div class="cards">
      <DashboardCard title="Active Patients" description="Current patients under your care" />
      <DashboardCard title="Appointments Today" description="Scheduled sessions for today" />
      <DashboardCard title="Pending Lab Results" description="Awaiting test results" />
    </div>
  </section>

  {#if isEdit}
    <div class="notice" role="status">
      <span>🩺 Edit mode active — make updates to patient records below.</span>
    </div>
  {/if}

  <!-- Tabs -->
  <nav class="tabs" aria-label="Doctor sections">
    <Tabs
      bind:activeTab
      items={[
        { id: 'patients', label: 'Patient List' },
        { id: 'records', label: 'Medical Records' },
        { id: 'privacy', label: 'Privacy & Consent' }
      ]}
    />
  </nav>

  <!-- Tab panels -->
  {#if activeTab === 'patients'}
    <section class="panel" aria-labelledby="patients">
      <h2 id="patients" style="margin:0 0 8px 0;">Patient List</h2>
      <p class="muted">Overview of patients assigned to you</p>

      <div class="table-wrap">
        <table class="table">
          <thead>
            <tr>
              <th>Patient</th>
              <th>Age</th>
              <th>Condition</th>
              <th>Status</th>
              <th>Last Visit</th>
            </tr>
          </thead>
          <tbody>
            <tr>
              <td>Jane Doe</td>
              <td>42</td>
              <td><span class="badge" data-variant="diagnosis">Diabetes</span></td>
              <td><span class="chip" data-variant="active">Active</span></td>
              <td>Oct 3, 2025</td>
            </tr>
            <tr>
              <td>Michael Lee</td>
              <td>36</td>
              <td><span class="badge" data-variant="medication">Hypertension</span></td>
              <td><span class="chip" data-variant="closed">Discharged</span></td>
              <td>Sept 29, 2025</td>
            </tr>
            <tr>
              <td>Amanda Ray</td>
              <td>55</td>
              <td><span class="badge" data-variant="diagnosis">Arthritis</span></td>
              <td><span class="chip" data-variant="active">Active</span></td>
              <td>Oct 2, 2025</td>
            </tr>
          </tbody>
        </table>
      </div>
    </section>
  {:else if activeTab === 'records'}
    <MedicalRecords />
  {:else}
    <PrivacyConsent />
  {/if}
</main>