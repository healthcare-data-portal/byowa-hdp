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

    onMount(() => {
        loadUsers();
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
            <div class="stat-value">—</div>
        </div>

        <div class="card">
            <div class="stat-title">
                <img
                        src="/src/lib/assets/pictures/document.png"
                        alt="FHIR messages"
                        class="stat-icon"
                />
                <span>FHIR Messages</span>
            </div>
            <div class="stat-value">—</div>
        </div>

        <div class="card">
            <div class="stat-title">
                <img
                        src="/src/lib/assets/pictures/shield.png"
                        alt="System health"
                        class="stat-icon"
                />
                <span>System Health</span>
            </div>
            <div class="stat-value">—</div>
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

    <div class="section">
        <div class="title">FHIR Message Processing</div>
        <div class="desc">Monitor FHIR to OMOP data conversion status</div>

        <div class="table-wrap">
            <table class="table">
                <thead>
                <tr>
                    <th class="th">Message ID</th>
                    <th class="th">Resource Type</th>
                    <th class="th">Patient ID</th>
                    <th class="th">Timestamp</th>
                    <th class="th">Status</th>
                    <th class="th">OMOP Mapped</th>
                </tr>
                </thead>
                <tbody></tbody>
            </table>
        </div>

        <div class="empty">No messages to display yet.</div>
    </div>
</div>
