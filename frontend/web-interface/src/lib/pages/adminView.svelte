<script>
    import Header from '../components/Header.svelte';
    import '/src/lib/styles/adminView.css';
    import { onMount } from 'svelte';
    import UserRow from '$lib/components/UserRow.svelte';

    const API = 'http://localhost:8080/api';
    const ROLES = ['PATIENT', 'DOCTOR', 'ADMIN'];

    let loading = true;
    let error = '';
    let users = [];
    let filter = '';
    let savingIds = new Set();

    let activeRecords = 0;
    let loadingRecords = true;

    let showUploadDialog = false;
    let uploadStatus = 'idle';
    let uploadResult = null;

    let selectedPatientId = '';
    let selectedDoctorId = '';
    let assigning = false;
    let assignMessage = '';

    function authHeaders() {
        const token = localStorage.getItem('token');
        return {
            'Content-Type': 'application/json',
            'Authorization': token ? `Bearer ${token}` : ''
        };
    }

    async function loadUsers() {
        loading = true;
        error = '';
        savingIds = new Set();

        try {
            const res = await fetch(`${API}/admin/users`, { headers: authHeaders() });
            if (!res.ok) {
                throw new Error((await res.text()) || `Failed to load users (${res.status})`);
            }
            users = await res.json();
        } catch (e) {
            error = e.message || 'Failed to load users';
        } finally {
            loading = false;
        }
    }

    async function handleSave(event) {
        const { id, role } = event.detail || {};
        if (!id || !role) return;

        const next = new Set(savingIds);
        next.add(id);
        savingIds = next;

        try {
            const res = await fetch(`${API}/admin/role`, {
                method: 'POST',
                headers: authHeaders(),
                body: JSON.stringify({ userId: id, role })
            });

            if (!res.ok) {
                const text = await res.text();
                throw new Error(text || 'Failed to update role');
            }

            users = users.map((u) => (u.id === id ? { ...u, role } : u));
        } catch (e) {
            error = e.message || 'Failed to update user role';
        } finally {
            const after = new Set(savingIds);
            after.delete(id);
            savingIds = after;
        }
    }

    async function assignPatient() {
        assignMessage = '';
        if (!selectedPatientId || !selectedDoctorId) {
            assignMessage = 'Select both patient and doctor';
            return;
        }

        assigning = true;
        try {
            const res = await fetch(`${API}/admin/assignments`, {
                method: 'POST',
                headers: authHeaders(),
                body: JSON.stringify({
                    patientId: Number(selectedPatientId),
                    doctorId: Number(selectedDoctorId)
                })
            });

            if (!res.ok) {
                const text = await res.text();
                throw new Error(text || 'Failed to assign');
            }

            assignMessage = 'Patient assigned to doctor successfully';
        } catch (e) {
            assignMessage = e.message || 'Failed to assign';
        } finally {
            assigning = false;
        }
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

    async function handleFileUpload(event) {
        const files = Array.from(event.target.files || []);
        if (files.length === 0) return;

        uploadStatus = 'processing';
        uploadResult = null;

        const isSingle = files.length === 1;
        let okCount = 0;
        let failCount = 0;
        let singleServerMsg = null;

        try {
            for (const file of files) {
                try {
                    const fileText = await file.text();
                    const fhirData = JSON.parse(fileText);

                    const endpoint = `${API}/fhir/import`;
                    const res = await authFetch(endpoint, {
                        method: 'POST',
                        headers: { 'Content-Type': 'application/json' },
                        body: JSON.stringify(fhirData)
                    });

                    if (!res.ok) {
                        const errText = await res.text().catch(() => null);
                        throw new Error(errText || `Import failed (${res.status})`);
                    }

                    const serverMsg = await res.text().catch(() => 'Import successful');
                    okCount++;

                    if (isSingle) {
                        singleServerMsg = serverMsg;
                    }
                } catch (fileErr) {
                    failCount++;
                    if (isSingle) throw fileErr;
                }
            }

            // Refresh records count after successful upload
            if (okCount > 0) {
                await loadRecordsCount();
            }

            if (isSingle) {
                uploadStatus = 'success';
                uploadResult = {
                    message: singleServerMsg || 'FHIR resource imported successfully.'
                };
            } else {
                uploadStatus = okCount > 0 ? 'success' : 'error';
                uploadResult = {
                    message: `Uploaded ${files.length} file(s): ${okCount} successful, ${failCount} failed.`
                };
            }
        } catch (err) {
            uploadStatus = 'error';
            uploadResult = {
                message: err?.message || 'Failed to process FHIR file(s). Check format and try again.'
            };
        }

        event.target.value = '';
    }

    function closeUploadDialog() {
        showUploadDialog = false;
        uploadStatus = 'idle';
        uploadResult = null;
    }

    async function loadRecordsCount() {
        loadingRecords = true;
        try {
            const res = await fetch(`${API}/admin/stats/records`, { headers: authHeaders() });
            if (res.ok) {
                const data = await res.json();
                activeRecords = data.total || 0;
            }
        } catch (e) {
            console.error('Failed to load records count', e);
        } finally {
            loadingRecords = false;
        }
    }

    onMount(() => {
        loadUsers();
        loadRecordsCount();
    });

    $: patients = users.filter((u) => u.role === 'PATIENT');
    $: doctors = users.filter((u) => u.role === 'DOCTOR');

    $: totalUsers = users.length;

    $: filtered = users.filter((u) => {
        if (!filter) return true;
        const q = filter.toLowerCase();
        const email = (u.email || u.username || '').toLowerCase();
        const role = (u.role || '').toLowerCase();
        const idStr = String(u.id || '');
        return email.includes(q) || role.includes(q) || idStr.includes(q);
    });
</script>

<Header role="admin" roleIcon="/src/lib/assets/pictures/white_shield.png" />

<div class="page-wrap">
    <div class="h1">Admin Dashboard</div>
    <div class="sub">Manage users, roles, and system settings</div>

    <div class="row">
        <div class="card">
            <div class="stat-title">
                <img
                        src="/src/lib/assets/pictures/users.png"
                        alt="Total users"
                        class="stat-icon"
                />
                <span>Total Users</span>
            </div>
            <div class="stat-value">{loading ? '…' : totalUsers}</div>
        </div>

        <div class="card">
            <div class="stat-title">
                <img
                        src="/src/lib/assets/pictures/database-management.png"
                        alt="Active records"
                        class="stat-icon"
                />
                <span>Active Records</span>
            </div>
            <div class="stat-value">{loadingRecords ? '…' : activeRecords}</div>
        </div>

        <div class="card">
            <div class="stat-title">
                <img
                        src="/src/lib/assets/pictures/download-black.png"
                        alt="Upload"
                        class="stat-icon"
                />
                <span>Upload FHIR</span>
            </div>
            <button
                    class="btn primary"
                    style="margin-top:0.75rem;width:100%;"
                    on:click={() => (showUploadDialog = true)}
            >
                Import Data
            </button>
        </div>
    </div>

    <div class="section">
        <div class="title">User Management</div>
        <div class="desc">Change roles and manage access</div>

        <div class="toolbar">
            <input
                    class="input"
                    placeholder="Search users..."
                    bind:value={filter}
            />
            <button class="btn primary" on:click={loadUsers}>Reload</button>
        </div>

        {#if error}
            <div class="empty" style="color:#b91c1c">Error: {error}</div>
        {/if}

        {#if loading}
            <div class="empty">Loading users…</div>
        {:else if filtered.length === 0}
            <div class="empty">No users to display.</div>
        {:else}
            <div class="table-wrap">
                <table class="table">
                    <thead>
                    <tr>
                        <th class="th">ID</th>
                        <th class="th">Email</th>
                        <th class="th">Role</th>
                        <th class="th">Actions</th>
                    </tr>
                    </thead>
                    <tbody>
                    {#each filtered as u (u.id)}
                        <UserRow
                                user={u}
                                roles={ROLES}
                                saving={savingIds.has(u.id)}
                                on:save={handleSave}
                        />
                    {/each}
                    </tbody>
                </table>
            </div>
        {/if}
    </div>

    <div class="section">
        <div class="assign-header">
            <div>
                <div class="title">Assign Patients to Doctors</div>
                <div class="desc">
                    Link patient accounts to doctor accounts. Only users with the
                    correct roles are shown.
                </div>
            </div>
            <div class="assign-stats">
                <span class="pill">
                    Patients: {patients.length}
                </span>
                <span class="pill">
                    Doctors: {doctors.length}
                </span>
            </div>
        </div>

        <div
                class="assign-row"
                style="display:grid;grid-template-columns:repeat(auto-fit,minmax(220px,1fr));gap:1rem;align-items:flex-end;margin-top:1rem;"
        >
            <div>
                <div class="field-label">Patient</div>
                <select class="input" bind:value={selectedPatientId}>
                    <option value="">Select patient</option>
                    {#each patients as p}
                        <option value={p.id}>
                            {(p.email || p.username) ?? 'User'} (#{p.id})
                        </option>
                    {/each}
                </select>
            </div>

            <div>
                <div class="field-label">Doctor</div>
                <select class="input" bind:value={selectedDoctorId}>
                    <option value="">Select doctor</option>
                    {#each doctors as d}
                        <option value={d.id}>
                            {(d.email || d.username) ?? 'User'} (#{d.id})
                        </option>
                    {/each}
                </select>
            </div>

            <div style="display:flex;flex-direction:column;gap:0.5rem;">
                <button
                        class="btn primary"
                        disabled={assigning}
                        on:click={assignPatient}
                >
                    {assigning ? 'Assigning…' : 'Assign'}
                </button>

                {#if assignMessage}
                    <div
                            class="assign-message"
                            style="font-size:0.875rem;color:#111827;"
                    >
                        {assignMessage}
                    </div>
                {/if}
            </div>
        </div>
    </div>

</div>

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
                width:min(600px,95%);
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
                        Upload FHIR files. Patients will not be auto-assigned to any doctor.
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
                                    multiple
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
                                <div class="muted" style="font-size:0.95rem;">
                                    {uploadResult?.message}
                                </div>
                            </div>
                        </div>
                        <button class="btn primary" on:click={closeUploadDialog}>
                            Close
                        </button>
                    </div>
                {:else if uploadStatus === 'error'}
                    <div style="display:flex;flex-direction:column;gap:0.5rem;">
                        <div
                                style="
                                display:flex;
                                gap:0.75rem;
                                align-items:center;
                                padding:0.75rem;
                                border-radius:6px;
                                background:#fef2f2;
                                border:1px solid #fecaca;
                            "
                        >
                            <div>❌</div>
                            <div>
                                <div style="font-weight:600;">Upload Failed</div>
                                <div class="muted" style="font-size:0.95rem;">
                                    {uploadResult?.message}
                                </div>
                            </div>
                        </div>
                        <button class="btn primary" on:click={() => (uploadStatus = 'idle')}>
                            Try Again
                        </button>
                    </div>
                {/if}
            </div>
        </div>
    </div>
{/if}
