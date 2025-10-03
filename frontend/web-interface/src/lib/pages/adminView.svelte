<script lang="ts">
  // note the relative path from src/lib/pages → src/styles
  import '/src/lib/styles/adminView.css';

  let stats = { totalUsers: 5, activeRecords: 2, fhirMessages: 2, systemHealth: 98 };

  type Role = 'admin' | 'doctor' | 'patient';
  type User = { id: string; name: string; email: string; role: Role; active: boolean; created: string; };
  let users: User[] = [
    { id:'u1', name:'Sarah Administrator', email:'admin@hospital.com', role:'admin',  active:true,  created:'15/01/2024' },
    { id:'u2', name:'Dr. John Smith',      email:'dr.smith@hospital.com', role:'doctor', active:true,  created:'20/01/2024' },
    { id:'u3', name:'Dr. Emily Johnson',   email:'dr.johnson@hospital.com', role:'doctor', active:true,  created:'01/02/2024' },
    { id:'u4', name:'Michael Brown',       email:'patient1@email.com', role:'patient', active:false, created:'01/03/2024' },
    { id:'u5', name:'Lisa Wilson',         email:'patient2@email.com', role:'patient', active:true,  created:'05/03/2024' }
  ];

  type FhirRow = { id:string; resource:string; patientId:string; timestamp:string; status:'Pending'|'Processed'; mapped:'Mapped'|'Unmapped'; };
  let fhirRows: FhirRow[] = [
    { id:'fhir-msg-1', resource:'Patient',     patientId:'patient-1', timestamp:'25/03/2024, 10:30:00', status:'Pending',   mapped:'Unmapped' },
    { id:'fhir-msg-2', resource:'Observation', patientId:'patient-1', timestamp:'25/03/2024, 11:15:00', status:'Processed', mapped:'Mapped'  }
  ];

  function toggleActive(u: User) { u.active = !u.active; }
  function viewDetails(u: User) { alert(`(demo) View details for ${u.name}`); }
  function createUser() { alert('(demo) Create New User'); }
</script>

<div class="admin-view">
  <header class="topbar">
    <div class="left">
      <span class="brand">Healthcare Data Portal</span>
      <span class="role-chip">Admin</span>
    </div>
    <div class="right">
      <div class="profile">
        <div class="avatar">SA</div>
        <div class="meta">
          <div class="name">Sarah Administrator</div>
          <div class="email">admin@hospital.com</div>
        </div>
      </div>
      <button class="btn ghost">Logout</button>
    </div>
  </header>

  <main class="page">
    <div class="page-head">
      <div>
        <h1>Admin Dashboard</h1>
        <p class="muted">Manage users, roles, and system settings</p>
      </div>
      <button class="btn primary" on:click={createUser}>Create New User</button>
    </div>

    <section class="cards">
      <div class="card"><div class="card-title">Total Users</div><div class="card-value">{stats.totalUsers}</div><div class="card-sub">Registered system users</div></div>
      <div class="card"><div class="card-title">Active Records</div><div class="card-value">{stats.activeRecords}</div><div class="card-sub">Active medical records</div></div>
      <div class="card"><div class="card-title">FHIR Messages</div><div class="card-value">{stats.fhirMessages}</div><div class="card-sub">FHIR messages processed</div></div>
      <div class="card"><div class="card-title">System Health</div><div class="card-value">{stats.systemHealth}%</div><div class="card-sub">Overall system uptime</div></div>
    </section>

    <section class="panel">
      <div class="panel-head">
        <div>
          <h2>User Management</h2>
          <p class="muted">Manage user accounts, roles, and permissions</p>
        </div>
      </div>
      <div class="table-wrap">
        <table class="table">
          <thead>
            <tr><th class="left">Name</th><th class="left">Email</th><th>Role</th><th>Status</th><th>Created</th><th class="right">Actions</th></tr>
          </thead>
          <tbody>
            {#each users as u}
              <tr>
                <td class="left">{u.name}</td>
                <td class="left">{u.email}</td>
                <td><span class="role-tag {u.role}">{u.role}</span></td>
                <td>
                  <label class="switch">
                    <input type="checkbox" checked={u.active} on:change={() => toggleActive(u)} />
                    <span class="slider"></span>
                  </label>
                  <span class="status-text">{u.active ? 'Active' : 'Inactive'}</span>
                </td>
                <td>{u.created}</td>
                <td class="right"><button class="btn" on:click={() => viewDetails(u)}>View Details</button></td>
              </tr>
            {/each}
          </tbody>
        </table>
      </div>
    </section>

    <section class="panel">
      <div class="panel-head">
        <div>
          <h2>FHIR Message Processing</h2>
          <p class="muted">Monitor FHIR to OMOP data conversion status</p>
        </div>
      </div>
      <div class="table-wrap">
        <table class="table">
          <thead>
            <tr><th class="left">Message ID</th><th>Resource Type</th><th>Patient ID</th><th>Timestamp</th><th>Status</th><th>OMOP Mapped</th></tr>
          </thead>
          <tbody>
            {#each fhirRows as r}
              <tr>
                <td class="left">{r.id}</td>
                <td>{r.resource}</td>
                <td>{r.patientId}</td>
                <td>{r.timestamp}</td>
                <td><span class="badge {r.status === 'Processed' ? 'ok' : 'pending'}">{r.status}</span></td>
                <td><span class="badge {r.mapped === 'Mapped' ? 'ok' : 'pending'}">{r.mapped}</span></td>
              </tr>
            {/each}
          </tbody>
        </table>
      </div>
    </section>
  </main>
</div>
