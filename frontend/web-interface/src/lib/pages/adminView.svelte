<script>
  // no data fetching here, just structure & styles
  import '/src/lib/styles/adminView.css';
</script>

<div class="page-wrap">
  <div class="h1">Admin Dashboard</div>
  <div class="sub">Manage users, roles, and system settings</div>

  <!-- Stats row -->
  <div class="row">
    <div class="card">
      <div class="stat-title">👥 Total Users</div>
      <div class="stat-value">—</div>
    </div>
    <div class="card">
      <div class="stat-title">📈 Active Records</div>
      <div class="stat-value">—</div>
    </div>
    <div class="card">
      <div class="stat-title">🧪 FHIR Messages</div>
      <div class="stat-value">—</div>
    </div>
    <div class="card">
      <div class="stat-title">🛡️ System Health</div>
      <div class="stat-value">—</div>
    </div>
  </div>

  <!-- User Management -->
  <div class="section">
    <div class="title">User Management</div>
    <div class="desc">Manage user accounts, roles, and permissions</div>

    <div class="toolbar">
      <input class="input" placeholder="Search users..." />
      <button class="btn primary">Create New User</button>
    </div>

    <div class="table-wrap">
      <table class="table">
        <thead>
          <tr>
            <th class="th">Name</th>
            <th class="th">Email</th>
            <th class="th">Role</th>
            <th class="th">Status</th>
            <th class="th">Created</th>
            <th class="th">Actions</th>
          </tr>
        </thead>
        <tbody>
          <!-- no rows; you’ll render <tr class="tr">...</tr> here from real data -->
        </tbody>
      </table>
    </div>

    <div class="empty">No users to display yet.</div>
  </div>

  <!-- FHIR Message Processing -->
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
        <tbody>
          <!-- no rows; map your data to <tr class="tr">...</tr> -->
        </tbody>
      </table>
    </div>

    <div class="empty">No messages to display yet.</div>
  </div>
</div>
