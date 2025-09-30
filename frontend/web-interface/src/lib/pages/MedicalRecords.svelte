<script>
  /**
   * UI-only Medical Records page.
   * - No mock data here. Pass real records later via a prop or a store.
   * - Shows an empty state when `records.length === 0`.
   */

  export let records = []; // [{ date, type, title, description, status, doctor }, ...]

  // Optional column labels (easy to translate later)
  const cols = ['Date', 'Type', 'Title', 'Description', 'Status', 'Doctor'];
</script>

<section class="panel" aria-labelledby="medical-records-title">
  <h2 id="medical-records-title" style="margin:0 0 6px 0;">My Medical Records</h2>
  <p class="muted" style="margin:0 0 12px 0;">View your complete medical history and records</p>

  {#if !records || records.length === 0}
    <div class="empty">
      <div class="empty-icon">🗂️</div>
      <div class="empty-title">No medical records yet</div>
      <div class="muted">When your backend is connected, records will appear here automatically.</div>
    </div>
  {:else}
    <div class="table-wrap" role="region" aria-label="Medical records table">
      <table class="table">
        <thead>
          <tr>
            {#each cols as c}<th scope="col">{c}</th>{/each}
          </tr>
        </thead>
        <tbody>
          {#each records as r}
            <tr>
              <td data-label="Date">{r.date}</td>
              <td data-label="Type">
                <span class="badge" data-variant={r.type?.toLowerCase() || 'default'}>{r.type}</span>
              </td>
              <td data-label="Title">{r.title}</td>
              <td data-label="Description" class="truncate">{r.description}</td>
              <td data-label="Status">
                <span class="chip" data-variant={r.status?.toLowerCase() || 'default'}>{r.status}</span>
              </td>
              <td data-label="Doctor">{r.doctor}</td>
            </tr>
          {/each}
        </tbody>
      </table>
    </div>
  {/if}
</section>
