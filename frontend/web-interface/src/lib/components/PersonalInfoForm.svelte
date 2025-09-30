<script>
  // Purely presentational form. No submission yet.
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
  const noop = (e) => { e.preventDefault(); };
</script>
<form on:submit|preventDefault={noop} class="grid-2" novalidate>
  <div class="field">
    <label for="fullName">Full Name</label>
    <input id="fullName" class="input" bind:value={form.fullName} autocomplete="name" placeholder="Full Name" />
  </div>
  <div class="field">
    <label for="email">Email Address</label>
    <input id="email" class="input" type="email" bind:value={form.email} autocomplete="email" placeholder="email@example.com" />
  </div>
  <div class="field">
    <label for="phone">Phone Number</label>
    <input id="phone" class="input" type="tel" bind:value={form.phone} autocomplete="tel" placeholder="+1 555 0123" />
  </div>
  <div class="field">
    <label for="dob">Date of Birth</label>
    <input id="dob" class="input" type="date" bind:value={form.dob} />
    <div class="muted">Date of birth may be read-only depending on policy.</div>
  </div>
  <div class="field">
    <label for="ssn">Social Security Number</label>
    <input id="ssn" class="input" bind:value={form.ssnMasked} aria-describedby="ssn-help" placeholder="***-**-1234" />
    <div id="ssn-help" class="muted">Display masked. Handle securely on the server.</div>
  </div>
  <div class="field">
    <label for="gender">Gender</label>
    <input id="gender" class="input" bind:value={form.gender} placeholder="—" />
  </div>

  <div class="divider grid-2" style="grid-column: 1/-1;"></div>

  <div style="grid-column: 1/-1;">
    <h3 style="margin: 0 0 8px 0;">Address Information</h3>
  </div>

  <div class="field">
    <label for="street">Street Address</label>
    <input id="street" class="input" bind:value={form.address.street} autocomplete="address-line1" placeholder="123 Main St" />
  </div>
  <div class="field">
    <label for="city">City</label>
    <input id="city" class="input" bind:value={form.address.city} autocomplete="address-level2" placeholder="Springfield" />
  </div>
  <div class="field">
    <label for="state">State</label>
    <input id="state" class="input" bind:value={form.address.state} autocomplete="address-level1" placeholder="IL" />
  </div>
  <div class="field">
    <label for="zip">ZIP Code</label>
    <input id="zip" class="input" bind:value={form.address.zip} autocomplete="postal-code" placeholder="62701" />
  </div>
  <div class="field">
    <label for="country">Country</label>
    <input id="country" class="input" bind:value={form.address.country} autocomplete="country" placeholder="USA" />
  </div>

  <div class="divider grid-2" style="grid-column: 1/-1;"></div>

  <div style="grid-column: 1/-1;">
    <h3 style="margin: 0 0 8px 0;">Emergency Contact</h3>
  </div>
  <div class="field">
    <label for="ec-name">Contact Name</label>
    <input id="ec-name" class="input" bind:value={form.emergency.name} placeholder="Jane Brown" />
  </div>
  <div class="field">
    <label for="ec-phone">Contact Phone</label>
    <input id="ec-phone" class="input" type="tel" bind:value={form.emergency.phone} placeholder="+1 555 0124" />
  </div>
  <div class="field">
    <label for="ec-rel">Relationship</label>
    <input id="ec-rel" class="input" bind:value={form.emergency.relation} placeholder="Spouse" />
  </div>

  <div style="grid-column: 1/-1; display:flex; justify-content:flex-end; gap:8px; margin-top: 8px;">
    <button type="button" class="btn ghost">Reset</button>
    <button type="submit" class="btn primary" aria-disabled="true" disabled>Save Changes</button>
  </div>
</form>
