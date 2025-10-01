<script>
  import { createEventDispatcher } from 'svelte';

  // Purely presentational; no API calls.
  export let editing = false;

  const dispatch = createEventDispatcher();

  let form = {
    fullName: '',
    email: '',
    phone: '',
    dob: '',
    ssnMasked: '',
    gender: '',
    address: { street: '', city: '', state: '', zip: '', country: '' },
    emergency: { name: '', phone: '', relation: '' }
  };

  // Keep a snapshot of the original values when entering edit mode
  let snapshot = null;
  let wasEditing = false;

  $: if (editing && !wasEditing) {
    snapshot = JSON.parse(JSON.stringify(form));
    wasEditing = true;
  } else if (!editing && wasEditing) {
    wasEditing = false;
    snapshot = null;
  }

  function onSave(e) {
    e.preventDefault();
    dispatch('save', { form });
  }

  function onCancel() {
    if (snapshot) {
      form = JSON.parse(JSON.stringify(snapshot)); // reset fields
    }
    dispatch('cancel');
  }
</script>

<form class="grid-2" novalidate on:submit|preventDefault={onSave}>
  <!-- Left column -->
  <div class="field">
    <label for="fullName">Full Name</label>
    <input id="fullName" class="input" bind:value={form.fullName} autocomplete="name" disabled={!editing} />
  </div>

  <div class="field">
    <label for="email">Email Address</label>
    <input id="email" class="input" type="email" bind:value={form.email} autocomplete="email" disabled={!editing} />
  </div>

  <div class="field">
    <label for="phone">Phone Number</label>
    <input id="phone" class="input" type="tel" bind:value={form.phone} autocomplete="tel" disabled={!editing} />
  </div>

  <div class="field">
    <label for="dob">Date of Birth</label>
    <input id="dob" class="input" type="date" bind:value={form.dob} disabled={!editing} />
    <div class="muted">Date of birth cannot be changed</div>
  </div>

  <div class="field">
    <label for="ssn">Social Security Number</label>
    <input id="ssn" class="input" bind:value={form.ssnMasked} aria-describedby="ssn-help" placeholder="***-**-1234" disabled={!editing} />
    <div id="ssn-help" class="muted">Enter your full SSN or update masked version</div>
  </div>

  <div class="field">
    <label for="gender">Gender</label>
    <input id="gender" class="input" bind:value={form.gender} disabled={!editing} />
  </div>

  <div class="divider grid-2" style="grid-column: 1/-1;"></div>

  <div style="grid-column: 1/-1;">
    <h3 style="margin: 0 0 8px 0;">Address Information</h3>
  </div>

  <div class="field">
    <label for="street">Street Address</label>
    <input id="street" class="input" bind:value={form.address.street} autocomplete="address-line1" disabled={!editing} />
  </div>

  <div class="field">
    <label for="city">City</label>
    <input id="city" class="input" bind:value={form.address.city} autocomplete="address-level2" disabled={!editing} />
  </div>

  <div class="field">
    <label for="state">State</label>
    <input id="state" class="input" bind:value={form.address.state} autocomplete="address-level1" disabled={!editing} />
  </div>

  <div class="field">
    <label for="zip">ZIP Code</label>
    <input id="zip" class="input" bind:value={form.address.zip} autocomplete="postal-code" disabled={!editing} />
  </div>

  <div class="field">
    <label for="country">Country</label>
    <input id="country" class="input" bind:value={form.address.country} autocomplete="country" disabled={!editing} />
  </div>

  <div class="divider grid-2" style="grid-column: 1/-1;"></div>

  <div style="grid-column: 1/-1;">
    <h3 style="margin: 0 0 8px 0;">Emergency Contact</h3>
  </div>

  <div class="field">
    <label for="ec-name">Contact Name</label>
    <input id="ec-name" class="input" bind:value={form.emergency.name} disabled={!editing} />
  </div>

  <div class="field">
    <label for="ec-phone">Contact Phone</label>
    <input id="ec-phone" class="input" type="tel" bind:value={form.emergency.phone} disabled={!editing} />
  </div>

  <div class="field">
    <label for="ec-rel">Relationship</label>
    <input id="ec-rel" class="input" bind:value={form.emergency.relation} disabled={!editing} />
  </div>

  {#if editing}
    <div class="actionbar" style="grid-column: 1/-1;">
      <div class="actionbar-content">
        <div class="muted">Review your information and save when ready.</div>
        <div class="actions">
          <button type="button" class="btn ghost" on:click={onCancel}>Cancel</button>
          <button type="submit" class="btn primary">Save Changes</button>
        </div>
      </div>
    </div>
  {/if}
</form>
