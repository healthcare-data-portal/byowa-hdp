<script>
  /**
   * UI-only Privacy & Consent page.
   * No persistence here; wire to API later if needed.
   */
  import { createEventDispatcher } from 'svelte';
  const dispatch = createEventDispatcher();

  // Local UI state (replace with real value from API later)
  let shareEnabled = true;

  function toggleShare() {
    shareEnabled = !shareEnabled;
    dispatch('toggle', { enabled: shareEnabled });
  }

  function downloadHistory() { dispatch('downloadHistory'); }
  function viewUsageLog() { dispatch('viewUsageLog'); }
</script>

<section class="panel" aria-labelledby="consent-title">
  <h2 id="consent-title" style="margin:0 0 6px 0;">Data Sharing Consent</h2>
  <p class="muted" style="margin:0 0 12px 0;">Control how your medical data is shared and used</p>

  <!-- Share switch card -->
  <div class="card row-between" role="group" aria-labelledby="share-title">
    <div>
      <div id="share-title" class="card-title">Share Medical Data</div>
      <div class="muted">Allow your medical data to be shared with authorized healthcare providers for improved care coordination.</div>
    </div>

    <!-- Accessible switch -->
    <button
      class="switch"
      type="button"
      role="switch"
      aria-checked={shareEnabled}
      aria-label="Share medical data"
      on:click={toggleShare}
      data-checked={shareEnabled}
    >
      <span class="knob"></span>
    </button>
  </div>

  <!-- Meaning block -->
  <div class="muted-panel" aria-label="What this means">
    <div class="panel-title">What This Means</div>
    <ul class="bullets">
      <li>Your data will be converted from FHIR to OMOP format for standardization</li>
      <li>Only authorized healthcare providers can access your information</li>
      <li>You can withdraw consent at any time</li>
      <li>Emergency access may override consent restrictions</li>
    </ul>
  </div>

  <!-- Info band -->
  <div class="info-band" role="note">
    <div class="panel-title" style="margin-bottom:6px;">Data Format Information</div>
    <div>
      Your medical data is stored in FHIR (Fast Healthcare Interoperability Resources) format and converted to OMOP
      (Observational Medical Outcomes Partnership) format for research and analytics while maintaining your privacy.
    </div>
  </div>

</section>
