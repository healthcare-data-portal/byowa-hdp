<script>
  import { onMount, createEventDispatcher } from 'svelte';

  const dispatch = createEventDispatcher();

  const API_BASE =
    import.meta.env.VITE_API_BASE ?? 'http://localhost:8080/api';

  let loading = false;
  let error = null;

  let records = [];

  // Details modal
  let showDetails = false;
  let selected = null;

  function formatDateTime(d) {
    if (!d) return '—';
    const dt = new Date(d);
    if (isNaN(dt.getTime())) return '—';
    return dt.toLocaleString(undefined, {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  }

  function authFetch(url, options = {}) {
    const token = typeof localStorage !== 'undefined' ? localStorage.getItem('token') : null;
    const headers = new Headers(options.headers || {});
    if (token) headers.set('Authorization', `Bearer ${token}`);

    return fetch(url, { ...options, headers, credentials: 'include' });
  }

  function measurementToRecord(m) {
    const dateIso = m.measurementDatetime
      ? m.measurementDatetime
      : (m.measurementDate ? `${m.measurementDate}T00:00:00Z` : new Date().toISOString());

    const title = (m.source || 'Measurement')
      .replace(/^LOINC:[^ ]+\s*/, '')
      .trim();

    const valueText = m.unit ? `${m.value} ${m.unit}` : `${m.value ?? '—'}`;

    return {
      id: `meas-${m.id}`,
      date: dateIso,
      type: 'measurement',
      title,
      description: valueText,
      status: 'Active',
      formats: ['OMOP'],
      omopData: m
    };
  }

  async function loadMyMeasurements() {
    loading = true;
    error = null;

    try {
      const url = `${API_BASE.replace(/\/$/, '')}/measurements/me?limit=1000`;
      const res = await authFetch(url);

      if (!res.ok) {
        const txt = await res.text().catch(() => null);
        throw new Error(txt || `Failed to load measurements (${res.status})`);
      }

      const data = await res.json();
      const list = Array.isArray(data) ? data : [];

      records = list.map(measurementToRecord);

      // sort newest first
      records.sort((a, b) => new Date(b.date) - new Date(a.date));

      // sag dem Parent die Count-Zahl (für DashboardCard)
      dispatch('count', { total: records.length });
    } catch (e) {
      error = e?.message || String(e);
      records = [];
      dispatch('count', { total: 0 });
    } finally {
      loading = false;
    }
  }

  function openDetails(r) {
    selected = r;
    showDetails = true;
  }

  function closeDetails() {
    showDetails = false;
    selected = null;
  }

  onMount(async () => {
    await loadMyMeasurements();
  });
</script>

<section class="panel" aria-labelledby="medical-records" style="margin-top:12px;">
  <div style="display:flex; align-items:center; justify-content:space-between; gap:12px; flex-wrap:wrap;">
    <div>
      <h2 id="medical-records" style="margin:0 0 6px 0;">Medical Records</h2>
      <p class="muted" style="margin:0;">
        Your latest measurements (OMOP)
      </p>
    </div>

    <div style="display:flex; gap:8px; align-items:center;">
      <span class="badge">{records.length} records</span>
      <button class="btn ghost" on:click={loadMyMeasurements} disabled={loading}>
        {loading ? 'Refreshing…' : 'Refresh'}
      </button>
    </div>
  </div>

  {#if loading}
    <div style="margin:12px 0">Loading measurements…</div>
  {:else if error}
    <div class="notice" style="color:var(--danger); margin-top:12px;">{error}</div>
  {/if}

  <div class="table-wrap" style="margin-top:12px;">
    <table class="table">
      <thead>
        <tr>
          <th>Type</th>
          <th>Title</th>
          <th>Date</th>
          <th>Status</th>
          <th style="text-align:right;">Actions</th>
        </tr>
      </thead>

      <tbody>
        {#if records.length === 0 && !loading}
          <tr>
            <td colspan="5" class="muted">No records to display yet.</td>
          </tr>
        {:else}
          {#each records as r}
            <tr>
              <td data-label="Type">
                <span class="badge">{r.type}</span>
              </td>

              <td data-label="Title">{r.title}</td>

              <td data-label="Date">{formatDateTime(r.date)}</td>

              <td data-label="Status">
                <span class="chip" data-variant="active">{r.status}</span>
              </td>

              <td data-label="Actions" style="text-align:right;">
                <button class="btn ghost" on:click={() => openDetails(r)}>
                  View details
                </button>
              </td>
            </tr>
          {/each}
        {/if}
      </tbody>
    </table>
  </div>
</section>

{#if showDetails && selected}
  <div
    class="upload-overlay"
    style="
      position:fixed;
      inset:0;
      background:rgba(0,0,0,0.35);
      display:flex;
      align-items:center;
      justify-content:center;
      z-index:80;
    "
    on:click={closeDetails}
  >
    <div
      class="card"
      style="
        width:min(820px,95%);
        max-height:85vh;
        overflow:auto;
        padding:1.25rem;
      "
      on:click|stopPropagation
    >
      <div style="display:flex; justify-content:space-between; align-items:flex-start; gap:12px;">
        <div>
          <h3 style="margin:0;">{selected.title}</h3>
          <div class="muted" style="margin-top:4px;">
            {formatDateTime(selected.date)}
          </div>
        </div>
        <button class="btn ghost" on:click={closeDetails} aria-label="Close">✕</button>
      </div>

      <div style="margin-top:12px; display:grid; grid-template-columns:1fr 1fr; gap:10px;">
        <div>
          <div class="muted" style="font-size:0.85rem;">Value</div>
          <div style="font-weight:700; font-size:1.15rem;">
            {selected?.omopData?.unit ? `${selected.omopData.value} ${selected.omopData.unit}` : (selected.omopData.value ?? '—')}
          </div>
        </div>

        <div>
          <div class="muted" style="font-size:0.85rem;">Source</div>
          <div style="font-family:monospace; font-size:0.95rem;">
            {selected?.omopData?.source ?? '—'}
          </div>
        </div>

        <div>
          <div class="muted" style="font-size:0.85rem;">Measurement date</div>
          <div>{selected?.omopData?.measurementDate ?? '—'}</div>
        </div>

        <div>
          <div class="muted" style="font-size:0.85rem;">Measurement datetime</div>
          <div>{selected?.omopData?.measurementDatetime ?? '—'}</div>
        </div>

        <div style="grid-column: 1 / -1;">
          <div class="muted" style="font-size:0.85rem; margin-bottom:6px;">Raw data</div>
          <pre style="margin:0; background:var(--input-background); padding:10px; border-radius:8px; overflow:auto;">
{JSON.stringify(selected.omopData, null, 2)}
          </pre>
        </div>
      </div>

      <div style="display:flex; justify-content:flex-end; gap:8px; margin-top:12px;">
        <button class="btn primary" on:click={closeDetails}>Close</button>
      </div>
    </div>
  </div>
{/if}
