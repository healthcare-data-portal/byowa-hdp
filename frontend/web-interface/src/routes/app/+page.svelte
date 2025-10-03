<script>
    import Header from '$lib/components/Header.svelte';
    import DashboardCard from '$lib/components/DashboardCard.svelte';
    import Tabs from '$lib/components/Tabs.svelte';
    import PersonalInfoForm from '$lib/components/PersonalInfoForm.svelte';
    import MedicalRecords from '$lib/pages/MedicalRecords.svelte';
    import PrivacyConsent from '$lib/pages/PrivacyConsent.svelte';

    let activeTab = 'personal';
    let isEdit = false;

    const toggleEdit = () => (isEdit = !isEdit);
    const handleSave = () => (isEdit = false);
    const handleCancel = () => (isEdit = false);
</script>

<Header />

<main class="container">
    <!-- Hero / Stats -->
    <section class="hero" aria-labelledby="dashboard-heading">
        <div style="display:flex; align-items:center; justify-content:space-between; gap:12px; flex-wrap: wrap;">
            <div>
                <h1 id="dashboard-heading" style="margin:0 0 6px 0;">My Health Dashboard</h1>
                <p class="muted" style="margin:0;">View and manage your personal health information</p>
            </div>
            <div style="display:flex; gap:8px;">
                <button class="btn ghost" aria-label="Export PDF">📥 Export PDF</button>
                <button class="btn ghost" aria-label="Detailed View">👁️ Detailed View</button>
                <button class="btn primary" aria-label="Edit Profile" on:click={toggleEdit}>
                    {isEdit ? 'Exit Edit' : 'Edit Profile'}
                </button>
            </div>
        </div>

        <div class="cards" role="list">
            <DashboardCard title="Total Records" description="Medical records on file" />
            <DashboardCard title="Active Treatments" description="Ongoing medical care" />
            <DashboardCard title="Data Consent" description="Data sharing consent status" format="text" />
        </div>
    </section>

    {#if isEdit}
        <div class="notice" role="status">
            <span>🔔 You are in edit mode. Make your changes and click <strong>“Save Changes”</strong> when finished.</span>
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
            <p class="muted">View and update your personal contact details and address</p>
            <PersonalInfoForm editing={isEdit} on:save={handleSave} on:cancel={handleCancel} />
        </section>
    {:else if activeTab === 'medical'}
        <MedicalRecords />
    {:else}
        <PrivacyConsent />
    {/if}
</main>
