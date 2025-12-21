<script>
    import Header from '$lib/components/Header.svelte';
    import DashboardCard from '$lib/components/DashboardCard.svelte';
    import Tabs from '$lib/components/Tabs.svelte';
    import { onMount } from 'svelte';

    const API_BASE =
        import.meta.env.VITE_API_BASE ?? 'http://localhost:8080/api';

    let activeTab = 'patients';
    let isEdit = false;

    let showUploadDialog = false;
    let uploadStatus = 'idle';
    let uploadResult = null;

    const toggleEdit = () => (isEdit = !isEdit);

    let provider = null;
    let loadingProvider = true;
    let providerError = null;

    let patients = [];
    let records = [];

    $: patientCount = patients.length || provider?.patientCount || 0;
    $: recordsCount = records.length || provider?.recordsCreated || 0;

    function formatDate(d) {
        if (!d) return '—';
        const dt = new Date(d);
        if (isNaN(dt.getTime())) return '—';
        return dt.toLocaleDateString(undefined, {
            year: 'numeric',
            month: 'short',
            day: 'numeric'
        });
    }

    function detectFHIRMessageType(fhirData) {
        const resourceType = (fhirData.resourceType || '').toLowerCase();
        switch (resourceType) {
            case 'condition':
                return 'diagnosis';
            case 'procedure':
                return 'procedure';
            case 'medicationrequest':
            case 'medicationstatement':
                return 'medication';
            case 'patient':
                return 'patient'
            case 'observation':
                if (fhirData.category?.[0]?.coding?.[0]?.code === 'laboratory')
                    return 'lab_result';
                return 'observation';
            default:
                return 'observation';
        }
    }

    function convertFHIRToOMOP(fhirData, recordType) {

        const resourceType = (fhirData.resourceType || '').toLowerCase();

        const patientSSN =
          fhirData.identifier?.[0]?.value || 'unknown';

        const personId =
          resourceType === 'patient'
            ? patientSSN
            : (fhirData.subject?.reference?.split('/')[1] ||
               fhirData.patient?.reference?.split('/')[1] ||
               'unknown');

        const baseOMOP = {
          person_id: personId,
          date: new Date().toISOString().split('T')[0]
        };

        switch (recordType) {
            case 'diagnosis':
                return {
                    ...baseOMOP,
                    condition_occurrence_id: Date.now(),
                    condition_concept_id: fhirData.code?.coding?.[0]?.code || '0',
                    condition_start_date: baseOMOP.date,
                    condition_type_concept_id: 32020
                };
            case 'medication':
                return {
                    ...baseOMOP,
                    drug_exposure_id: Date.now(),
                    drug_concept_id:
                        fhirData.medicationCodeableConcept?.coding?.[0]?.code || '0',
                    drug_exposure_start_date: baseOMOP.date,
                    drug_type_concept_id: 38000177
                };
            case 'lab_result':
                return {
                    ...baseOMOP,
                    measurement_id: Date.now(),
                    measurement_concept_id: fhirData.code?.coding?.[0]?.code || '0',
                    measurement_date: baseOMOP.date,
                    measurement_type_concept_id: 44818702
                };
            default:
                return baseOMOP;
        }
    }

    async function handleFileUpload(event) {
        const file = event.target.files?.[0];
        if (!file) return;

        uploadStatus = 'processing';
        uploadResult = null;

        try {
            const fileText = await file.text();
            const fhirData = JSON.parse(fileText);

            const recordType = detectFHIRMessageType(fhirData);
            const resourceType = (fhirData.resourceType || '').toLowerCase();
            const patientSSN =
              fhirData.identifier?.[0]?.value ||  // ggf. später gezielt filtern, siehe Hinweis unten
              'unknown';


            const patientId =
              resourceType === 'patient'
                ? patientSSN
                : (fhirData.subject?.reference?.split('/')[1] ||
                   fhirData.patient?.reference?.split('/')[1] ||
                   'unknown');

            const omopData = convertFHIRToOMOP(fhirData, recordType);

            const newRecord = {
                id: `record-${Date.now()}`,
                patientId,
                patientName:
                    patients.find((p) => String(p.id) === String(patientId))?.name ||
                    patientId,
                date: new Date().toISOString(),
                type: recordType,
                title:
                    fhirData.code?.coding?.[0]?.display ||
                    fhirData.medicationCodeableConcept?.coding?.[0]?.display ||
                    `${recordType.charAt(0).toUpperCase()}${recordType.slice(
                        1
                    )} Record`,
                description:
                    fhirData.note?.[0]?.text ||
                    fhirData.reasonCode?.[0]?.text ||
                    `Imported from FHIR ${fhirData.resourceType || 'resource'}`,
                fhirData,
                omopData,
                status: 'Active',
                formats: ['FHIR', 'OMOP']
            };

            // Füge lokal erstellten Record zur UI hinzu
            records = [newRecord, ...records];

            // Sende die FHIR-Nutzlast an das Backend-Import-Endpoint
            const endpoint = `${API_BASE.replace(/\/$/, '')}/fhir/import`;
            const res = await authFetch(endpoint, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(fhirData)
            });

            if (!res.ok) {
                const errText = await res.text().catch(() => null);
                throw new Error(errText || `Import failed (${res.status})`);
            }

            const serverMsg = await res.text().catch(() => 'Import successful');

            uploadStatus = 'success';
            uploadResult = {
                patientId,
                recordType,
                message: `Processed ${fhirData.resourceType || 'FHIR'} and converted to OMOP. Server: ${serverMsg}`
            };
        } catch (err) {
            uploadStatus = 'error';
            uploadResult = {
                message: err?.message || 'Failed to process FHIR file. Check format and try again.'
            };
        }

        event.target.value = '';
    }

    async function closeUploadDialog() {
        showUploadDialog = false;
        uploadStatus = 'idle';
        uploadResult = null;
    }

    async function authFetch(url, options = {}) {
        const token = localStorage.getItem('token');
        const headers = new Headers(options.headers || {});
        if (token) headers.set('Authorization', `Bearer ${token}`);

        const res = await fetch(url, {
            ...options,
            headers,
            credentials: 'include'
        });
        return res;
    }

    async function fetchProvider() {
        loadingProvider = true;
        providerError = null;

        try {
            const res = await authFetch(`${API_BASE}/providers/me`);
            if (!res.ok) throw new Error(`Failed to load provider (${res.status})`);
            provider = await res.json();
        } catch (err) {
            providerError = err.message || String(err);
            provider = null;
        } finally {
            loadingProvider = false;
        }
    }

    async function tryFetchPatients(id) {
        try {
            const res = await authFetch(`${API_BASE}/providers/${id}/patients`);
            if (!res.ok) return;
            const data = await res.json();
            if (Array.isArray(data)) {
                patients = data;
                if (provider) {
                    provider = { ...provider, patientCount: data.length };
                }
            }
        } catch {
        }
    }

    async function tryFetchRecords(id) {
        try {
            const res = await authFetch(`${API_BASE}/providers/${id}/records`);
            if (!res.ok) return;
            const data = await res.json();
            if (Array.isArray(data)) {
                records = data;
                if (provider) {
                    provider = { ...provider, recordsCreated: data.length };
                }
            }
        } catch {
        }
    }

    onMount(async () => {
        await fetchProvider();

        if (provider?.id) {
            await tryFetchPatients(provider.id);
            await tryFetchRecords(provider.id);
        }
    });
</script>

<Header role="doctor" roleIcon="/src/lib/assets/pictures/stethoscope.png" />

<main class="container">
    <section class="hero" aria-labelledby="doctor-dashboard">
        <div class="row-between">
            <div>
                <h1 id="doctor-dashboard" style="margin:0 0 6px 0;">Doctor Dashboard</h1>
                <p class="muted" style="margin:0;">
                    Manage patient care and medical records
                </p>
            </div>

            <div class="actions-row">
                <button
                        class="btn primary"
                        on:click={() => (showUploadDialog = true)}
                        aria-label="Upload FHIR Message"
                >
                    <img
                            src="/src/lib/assets/pictures/download.png"
                            alt="icon"
                            class="icon"
                            width="12"
                            height="12"
                    />
                    Upload FHIR Message
                </button>
            </div>
        </div>

        <div class="cards">
            <DashboardCard
                    title="My Patients"
                    description="Active patient records"
                    count={patientCount}
            />
            <DashboardCard
                    title="Records Created"
                    description="Medical records authored"
                    count={recordsCount}
            />
        </div>
    </section>

    {#if isEdit}
        <div class="notice" role="status">
            <span>🩺 Edit mode active — make updates to patient records below.</span>
        </div>
    {/if}

    <nav class="tabs" aria-label="Doctor sections">
        <Tabs
                bind:activeTab
                items={[
                { id: 'patients', label: `Patient Management (${patientCount})` },
                { id: 'records', label: `Medical Records (${recordsCount})` }
            ]}
        />
    </nav>

    {#if loadingProvider}
        <div style="margin:12px 0">Loading doctor info…</div>
    {:else if providerError}
        <div class="notice" style="color:var(--danger)">{providerError}</div>
    {:else if provider}
        <header
                class="doctor-header"
                style="display:flex;gap:12px;align-items:center;margin-top:12px;"
        >
            <div
                    class="avatar"
                    style="
                    width:48px;
                    height:48px;
                    border-radius:24px;
                    background:#eef;
                    display:flex;
                    align-items:center;
                    justify-content:center;
                    font-weight:700;
                "
            >
                {provider.initials ?? '?'}
            </div>
            <div>
                <div style="font-weight:700">{provider.providerName}</div>
                <div style="color:#666">{provider.providerSourceValue}</div>
            </div>
        </header>

        <section class="panel" style="margin-top:12px;">
            <h2 style="margin:0 0 6px 0;">Doctor details</h2>

            <div class="table-wrap">
                <table class="table">
                    <tbody>
                    <tr>
                        <th>Name</th>
                        <td>{provider.providerName}</td>
                    </tr>
                    <tr>
                        <th>Specialty concept</th>
                        <td>{provider.specialtyConceptId}</td>
                    </tr>
                    <tr>
                        <th>Care site</th>
                        <td>{provider.careSiteId}</td>
                    </tr>
                    <tr>
                        <th>Patients (count)</th>
                        <td>{patientCount}</td>
                    </tr>
                    <tr>
                        <th>Records created</th>
                        <td>{recordsCount}</td>
                    </tr>
                    </tbody>
                </table>
            </div>
        </section>
    {/if}

    {#if activeTab === 'patients'}
        <section class="panel" aria-labelledby="patients" style="margin-top:12px;">
            <h2 id="patients" style="margin:0 0 8px 0;">Patient Management</h2>
            <p class="muted">Find and manage your patients' medical information</p>

            <div class="table-wrap">
                <table class="table">
                    <thead>
                    <tr>
                        <th>Name</th>
                        <th>Email</th>
                        <th>Date of Birth</th>
                        <th>Gender</th>
                        <th>Consent Status</th>
                        <th>Actions</th>
                    </tr>
                    </thead>
                    <tbody>
                    {#if patients.length === 0}
                        <tr>
                            <td colspan="6" class="muted">
                                No patients to display yet.
                            </td>
                        </tr>
                    {:else}
                        {#each patients as p}
                            <tr>
                                <td data-label="Name">{p.name}</td>
                                <td data-label="Email">{p.email}</td>
                                <td data-label="Date of Birth">
                                    {formatDate(p.dob)}
                                </td>
                                <td data-label="Gender">{p.gender}</td>
                                <td data-label="Consent Status">
                                        <span
                                                class="badge"
                                                data-variant={p.consentGiven
                                                ? 'default'
                                                : 'secondary'}
                                        >
                                            {p.consentGiven ? 'Consented' : 'No Consent'}
                                        </span>
                                </td>
                                <td data-label="Actions">
                                    <button
                                            class="btn ghost"
                                            on:click={() => (activeTab = 'records')}
                                    >
                                        View Records
                                    </button>
                                </td>
                            </tr>
                        {/each}
                    {/if}
                    </tbody>
                </table>
            </div>
        </section>
    {/if}

    {#if activeTab === 'records'}
        <section
                class="panel"
                aria-labelledby="medical-records"
                style="margin-top:12px;"
        >
            <h2 id="medical-records" style="margin:0 0 8px 0;">Medical Records</h2>
            <p class="muted">
                View and manage medical records with FHIR → OMOP mapping
            </p>

            <div class="table-wrap">
                <table class="table">
                    <thead>
                    <tr>
                        <th>Patient</th>
                        <th>Type</th>
                        <th>Title</th>
                        <th>Date</th>
                        <th>Status</th>
                        <th>Data Format</th>
                    </tr>
                    </thead>
                    <tbody>
                    {#if records.length === 0}
                        <tr>
                            <td colspan="6" class="muted">
                                No records to display yet.
                            </td>
                        </tr>
                    {:else}
                        {#each records as r}
                            <tr>
                                <td data-label="Patient">{r.patientName}</td>
                                <td data-label="Type">
                                    <span class="badge">{r.type}</span>
                                </td>
                                <td data-label="Title">{r.title}</td>
                                <td data-label="Date">{formatDate(r.date)}</td>
                                <td data-label="Status">
                                        <span
                                                class="chip"
                                                data-variant={r.status?.toLowerCase() ===
                                            'active'
                                                ? 'active'
                                                : 'closed'}
                                        >
                                            {r.status}
                                        </span>
                                </td>
                                <td data-label="Data Format">
                                    <div
                                            style="
                                                display:flex;
                                                gap:.25rem;
                                                flex-wrap:wrap;
                                            "
                                    >
                                        {#each r.formats || [] as f}
                                            <span class="badge">{f}</span>
                                        {/each}
                                    </div>
                                </td>
                            </tr>
                        {/each}
                    {/if}
                    </tbody>
                </table>
            </div>
        </section>
    {/if}
</main>

{#if showUploadDialog}
    <div
            class="upload-overlay"
            style="
            position:fixed;
            inset:0;
            background:rgba(0,0,0,0.35);
            display:flex;
            align-items:center;
            justify-content:center;
            z-index:60;
        "
    >
        <div
                class="card"
                style="
                width:min(880px,95%);
                max-height:85vh;
                overflow:auto;
                padding:1.25rem;
            "
        >
            <div
                    style="
                    display:flex;
                    justify-content:space-between;
                    align-items:center;
                    margin-bottom:0.5rem;
                "
            >
                <div>
                    <h3 style="margin:0;">Upload FHIR Message</h3>
                    <p
                            class="muted"
                            style="margin:4px 0 0 0;font-size:0.95rem;"
                    >
                        Upload a FHIR message file. The system will detect the type
                        and convert to OMOP format.
                    </p>
                </div>
                <button
                        class="btn ghost"
                        on:click={closeUploadDialog}
                        aria-label="Close upload dialog"
                >
                    ✕
                </button>
            </div>

            <div style="margin-top:1rem;">
                {#if uploadStatus === 'idle'}
                    <label for="fhir-upload" style="display:block;cursor:pointer;">
                        <div
                                style="
                                border:2px dashed var(--border);
                                padding:1.25rem;
                                border-radius:8px;
                                text-align:center;
                            "
                        >
                            <div style="margin-bottom:0.5rem;">
                                <img src="/src/lib/assets/pictures/download-black.png" alt="Upload" style="width:48px;height:48px;" />
                            </div>
                            <div style="font-weight:600;">
                                Click to upload FHIR file
                            </div>
                            <div
                                    class="muted"
                                    style="font-size:0.9rem;margin-top:.25rem;"
                            >
                                or drag and drop (JSON .fhir)
                            </div>
                            <input
                                    id="fhir-upload"
                                    type="file"
                                    accept=".json,.fhir"
                                    on:change={handleFileUpload}
                                    style="display:none;"
                            />
                        </div>
                    </label>
                {:else if uploadStatus === 'processing'}
                    <div style="text-align:center;padding:2rem 1rem;">
                        Processing FHIR message…
                    </div>
                {:else if uploadStatus === 'success'}
                    <div style="display:flex;flex-direction:column;gap:0.5rem;">
                        <div
                                style="
                                display:flex;
                                gap:0.75rem;
                                align-items:center;
                                padding:0.75rem;
                                border-radius:6px;
                                background:#ecfdf5;
                                border:1px solid #bbf7d0;
                            "
                        >
                            <div>✅</div>
                            <div>
                                <div style="font-weight:600;">Upload Successful</div>
                                <div
                                        class="muted"
                                        style="font-size:0.95rem;"
                                >
                                    {uploadResult?.message}
                                </div>
                            </div>
                        </div>

                        {#if uploadResult?.patientId}
                            <div
                                    style="
                                    display:grid;
                                    grid-template-columns:1fr 1fr;
                                    gap:0.5rem;
                                "
                            >
                                <div>
                                    <div
                                            class="muted"
                                            style="font-size:0.85rem;"
                                    >
                                        Patient Social Security Number
                                    </div>
                                    <div
                                            style="
                                            font-family:monospace;
                                            padding:0.4rem;
                                            background:var(--input-background);
                                            border-radius:4px;
                                            margin-top:4px;
                                        "
                                    >
                                        {uploadResult.patientId}

                                    </div>
                                </div>
                                <div>
                                    <div
                                            class="muted"
                                            style="font-size:0.85rem;"
                                    >
                                        Record Type
                                    </div>
                                    <div
                                            style="
                                            font-family:monospace;
                                            padding:0.4rem;
                                            background:var(--input-background);
                                            border-radius:4px;
                                            margin-top:4px;
                                            text-transform:capitalize;
                                        "
                                    >
                                        {uploadResult.recordType}
                                    </div>
                                </div>
                            </div>
                        {/if}

                        <div style="display:flex;gap:0.5rem;margin-top:0.5rem;">
                            <button
                                    class="btn primary"
                                    on:click={() => {
                                    uploadStatus = 'idle';
                                    uploadResult = null;
                                }}
                            >
                                Upload Another File
                            </button>
                            <button
                                    class="btn ghost"
                                    on:click={closeUploadDialog}
                            >
                                Close
                            </button>
                        </div>
                    </div>
                {:else if uploadStatus === 'error'}
                    <div style="display:flex;flex-direction:column;gap:0.5rem;">
                        <div
                                style="
                                padding:0.75rem;
                                border-radius:6px;
                                background:#fff1f2;
                                border:1px solid #fecaca;
                                display:flex;
                                gap:0.75rem;
                                align-items:center;
                            "
                        >
                            <div>⚠️</div>
                            <div>
                                <div style="font-weight:600;">Upload Failed</div>
                                <div
                                        class="muted"
                                        style="font-size:0.95rem;"
                                >
                                    {uploadResult?.message}
                                </div>
                            </div>
                        </div>

                        <div style="display:flex;gap:0.5rem;">
                            <button
                                    class="btn ghost"
                                    on:click={() => {
                                    uploadStatus = 'idle';
                                    uploadResult = null;
                                }}
                            >
                                Try Again
                            </button>
                            <button
                                    class="btn ghost"
                                    on:click={closeUploadDialog}
                            >
                                Close
                            </button>
                        </div>
                    </div>
                {/if}
            </div>
        </div>
    </div>
{/if}
