<script>
  import Header from '$lib/components/Header.svelte';
  import DashboardCard from '$lib/components/DashboardCard.svelte';
  import Tabs from '$lib/components/Tabs.svelte';

  let activeTab = 'patients';
  let isEdit = false;

  // Upload dialog state
  let showUploadDialog = false;
  let uploadStatus = 'idle'; // 'idle' | 'processing' | 'success' | 'error'
  let uploadResult = null;

  const toggleEdit = () => (isEdit = !isEdit);

  const patients = [
    { id: 'p1', name: 'Jane Doe', email: 'jane.doe@example.com', dob: '1983-06-12', gender: 'Female', consentGiven: true },
    { id: 'p2', name: 'Michael Lee', email: 'michael.lee@example.com', dob: '1989-11-02', gender: 'Male', consentGiven: false },
    { id: 'p3', name: 'Amanda Ray', email: 'amanda.ray@example.com', dob: '1970-05-18', gender: 'Female', consentGiven: true }
  ];

  // records must be mutable (we'll add uploaded records here)
  let records = [
    { id: 'r1', patientId: 'p1', patientName: 'Jane Doe', type: 'Diagnosis', title: 'Diabetes', date: '2025-10-03', status: 'Active', formats: ['FHIR', 'OMOP'] },
    { id: 'r2', patientId: 'p2', patientName: 'Michael Lee', type: 'Medication', title: 'Hypertension Meds', date: '2025-09-29', status: 'Closed', formats: ['FHIR', 'OMOP'] },
    { id: 'r3', patientId: 'p3', patientName: 'Amanda Ray', type: 'Procedure', title: 'Knee Replacement', date: '2025-10-02', status: 'Active', formats: ['FHIR', 'OMOP'] }
  ];

  function formatDate(d) {
    const dt = new Date(d);
    return dt.toLocaleDateString(undefined, { year: 'numeric', month: 'short', day: 'numeric' });
  }

  // ----- FHIR helpers (lightweight port from your React example) -----
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
      case 'observation':
        // if lab category, mark as lab_result
        if (fhirData.category?.[0]?.coding?.[0]?.code === 'laboratory') return 'lab_result';
        return 'observation';
      default:
        return 'observation';
    }
  }

  function convertFHIRToOMOP(fhirData, recordType) {
    const baseOMOP = {
      person_id: fhirData.subject?.reference?.split('/')[1] || 'unknown',
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
          drug_concept_id: fhirData.medicationCodeableConcept?.coding?.[0]?.code || '0',
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

  // ----- File upload handler -----
  async function handleFileUpload(event) {
    const file = event.target.files?.[0];
    if (!file) return;

    uploadStatus = 'processing';
    uploadResult = null;

    try {
      const fileText = await file.text();
      const fhirData = JSON.parse(fileText);

      const recordType = detectFHIRMessageType(fhirData);

      // extract patient id from common FHIR locations
      const patientId =
        fhirData.subject?.reference?.split('/')[1] ||
        fhirData.patient?.reference?.split('/')[1] ||
        'unknown';

      const omopData = convertFHIRToOMOP(fhirData, recordType);

      const newRecord = {
        id: `record-${Date.now()}`,
        patientId,
        patientName: patients.find(p => p.id === patientId)?.name || patientId,
        date: new Date().toISOString(),
        type: recordType,
        title:
          fhirData.code?.coding?.[0]?.display ||
          fhirData.medicationCodeableConcept?.coding?.[0]?.display ||
          `${recordType.charAt(0).toUpperCase() + recordType.slice(1)} Record`,
        description:
          fhirData.note?.[0]?.text ||
          fhirData.reasonCode?.[0]?.text ||
          `Imported from FHIR ${fhirData.resourceType || 'resource'}`,
        fhirData,
        omopData,
        status: 'Active',
        formats: ['FHIR', 'OMOP']
      };

      // add to records list (newest first)
      records = [newRecord, ...records];

      uploadStatus = 'success';
      uploadResult = {
        patientId,
        recordType,
        message: `Successfully processed ${fhirData.resourceType || 'FHIR'} and converted to OMOP format`
      };
    } catch (err) {
      console.error('Error processing FHIR file:', err);
      uploadStatus = 'error';
      uploadResult = { message: 'Failed to process FHIR file. Please check the file format and try again.' };
    }

    // reset the input so same file can be re-uploaded if needed
    event.target.value = '';
  }

  function closeUploadDialog() {
    showUploadDialog = false;
    uploadStatus = 'idle';
    uploadResult = null;
  }
</script>

<Header />

<main class="container">
  <!-- Header Section -->
  <section class="hero" aria-labelledby="doctor-dashboard">
    <div class="row-between">
      <div>
        <h1 id="doctor-dashboard" style="margin:0 0 6px 0;">Doctor Dashboard</h1>
        <p class="muted" style="margin:0;">Manage patient care and medical records</p>
      </div>

      <!-- REPLACED: single Upload FHIR Message button (matches Figma) -->
      <div class="actions-row">
        <button class="btn primary" on:click={() => (showUploadDialog = true)} aria-label="Upload FHIR Message">
          📤 Upload FHIR Message
        </button>
      </div>
    </div>

    <div class="cards">
      <DashboardCard title="My Patients" description="Active patient records" />
      <DashboardCard title="Records Created" description="Medical records authored" />
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
        { id: 'patients', label: 'Patient Management' },
        { id: 'records', label: 'Medical Records' }
      ]}
    />
  </nav>

  <!-- Tab: Patient Management -->
  {#if activeTab === 'patients'}
    <section class="panel" aria-labelledby="patients">
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
            {#each patients as p}
              <tr>
                <td data-label="Name">{p.name}</td>
                <td data-label="Email">{p.email}</td>
                <td data-label="Date of Birth">{formatDate(p.dob)}</td>
                <td data-label="Gender">{p.gender}</td>
                <td data-label="Consent Status">
                  <span class="badge" data-variant={p.consentGiven ? 'default' : 'secondary'}>
                    {p.consentGiven ? 'Consented' : 'No Consent'}
                  </span>
                </td>
                <td data-label="Actions">
                  <button class="btn ghost" on:click={() => (activeTab = 'records')}>
                    View Records
                  </button>
                </td>
              </tr>
            {/each}
          </tbody>
        </table>
      </div>
    </section>
  {/if}

  <!-- Tab: Medical Records -->
  {#if activeTab === 'records'}
    <section class="panel" aria-labelledby="medical-records">
      <h2 id="medical-records" style="margin:0 0 8px 0;">Medical Records</h2>
      <p class="muted">View and manage medical records with FHIR to OMOP mapping</p>

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
            {#each records as r}
              <tr>
                <td data-label="Patient">{r.patientName}</td>
                <td data-label="Type"><span class="badge">{r.type}</span></td>
                <td data-label="Title">{r.title}</td>
                <td data-label="Date">{formatDate(r.date)}</td>
                <td data-label="Status">
                  <span class="chip" data-variant={r.status.toLowerCase() === 'active' ? 'active' : 'closed'}>
                    {r.status}
                  </span>
                </td>
                <td data-label="Data Format">
                  <div style="display:flex;gap:.25rem;flex-wrap:wrap;">
                    {#each r.formats as f}
                      <span class="badge">{f}</span>
                    {/each}
                  </div>
                </td>
              </tr>
            {/each}
          </tbody>
        </table>
      </div>
    </section>
  {/if}
</main>

<!-- Upload Dialog (simple inline dialog, uses existing classes) -->
{#if showUploadDialog}
  <div class="upload-overlay" style="position:fixed;inset:0;background:rgba(0,0,0,0.35);display:flex;align-items:center;justify-content:center;z-index:60;">
    <div class="card" style="width:min(880px,95%);max-height:85vh;overflow:auto;padding:1.25rem;">
      <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:0.5rem;">
        <div>
          <h3 style="margin:0;">Upload FHIR Message</h3>
          <p class="muted" style="margin:4px 0 0 0;font-size:0.95rem;">
            Upload a FHIR message file. The system will automatically detect the message type and convert it to OMOP format.
          </p>
        </div>
        <button class="btn ghost" on:click={closeUploadDialog} aria-label="Close upload dialog">✕</button>
      </div>

      <div style="margin-top:1rem;">
        {#if uploadStatus === 'idle'}
          <label for="fhir-upload" style="display:block;cursor:pointer;">
            <div style="border:2px dashed var(--border);padding:1.25rem;border-radius:8px;text-align:center;">
              <div style="font-size:1.5rem;margin-bottom:0.5rem;">📤</div>
              <div style="font-weight:600;">Click to upload FHIR file</div>
              <div class="muted" style="font-size:0.9rem;margin-top:.25rem;">or drag and drop (JSON .fhir)</div>
              <input id="fhir-upload" type="file" accept=".json,.fhir" on:change={handleFileUpload} style="display:none;" />
            </div>
          </label>

          <div style="margin-top:1rem;background:#f0f6ff;border:1px solid #dbeafe;padding:0.75rem;border-radius:6px;">
            <strong style="font-size:0.95rem;color:#083344;">How it works</strong>
            <ul style="margin:6px 0 0 16px;font-size:0.9rem;color:var(--muted-foreground);">
              <li>Upload a FHIR JSON file</li>
              <li>System detects resource type</li>
              <li>Converts FHIR data to OMOP format</li>
              <li>Links to a patient record (if possible)</li>
            </ul>
          </div>
        {:else if uploadStatus === 'processing'}
          <div style="text-align:center;padding:2rem 1rem;">
            <div style="margin-bottom:0.5rem;">Processing FHIR message…</div>
            <div class="muted" style="font-size:0.9rem;">Converting to OMOP format</div>
          </div>
        {:else if uploadStatus === 'success'}
          <div style="display:flex;flex-direction:column;gap:0.5rem;">
            <div style="display:flex;gap:0.75rem;align-items:center;padding:0.75rem;border-radius:6px;background:#ecfdf5;border:1px solid #bbf7d0;">
              <div>✅</div>
              <div>
                <div style="font-weight:600;">Upload Successful</div>
                <div class="muted" style="font-size:0.95rem;">{uploadResult?.message}</div>
              </div>
            </div>

            {#if uploadResult?.patientId}
              <div style="display:grid;grid-template-columns:1fr 1fr;gap:0.5rem;">
                <div>
                  <div class="muted" style="font-size:0.85rem;">Patient ID</div>
                  <div style="font-family:monospace;padding:0.4rem;background:var(--input-background);border-radius:4px;margin-top:4px;">
                    {uploadResult.patientId}
                  </div>
                </div>
                <div>
                  <div class="muted" style="font-size:0.85rem;">Record Type</div>
                  <div style="font-family:monospace;padding:0.4rem;background:var(--input-background);border-radius:4px;margin-top:4px;text-transform:capitalize;">
                    {uploadResult.recordType}
                  </div>
                </div>
              </div>
            {/if}

            <div style="display:flex;gap:0.5rem;margin-top:0.5rem;">
              <button class="btn primary" on:click={() => { uploadStatus = 'idle'; uploadResult = null; }}>Upload Another File</button>
              <button class="btn ghost" on:click={closeUploadDialog}>Close</button>
            </div>
          </div>
        {:else if uploadStatus === 'error'}
          <div style="display:flex;flex-direction:column;gap:0.5rem;">
            <div style="padding:0.75rem;border-radius:6px;background:#fff1f2;border:1px solid #fecaca;display:flex;gap:0.75rem;align-items:center;">
              <div>⚠️</div>
              <div>
                <div style="font-weight:600;">Upload Failed</div>
                <div class="muted" style="font-size:0.95rem;">{uploadResult?.message}</div>
              </div>
            </div>

            <div style="display:flex;gap:0.5rem;">
              <button class="btn ghost" on:click={() => { uploadStatus = 'idle'; uploadResult = null; }}>Try Again</button>
              <button class="btn ghost" on:click={closeUploadDialog}>Close</button>
            </div>
          </div>
        {/if}
      </div>
    </div>
  </div>
{/if}