<script>
  import { createEventDispatcher, onMount } from 'svelte';

  // control from parent
  export let open = false;

  const dispatch = createEventDispatcher();

  let name = '';
  let email = '';
  let role = 'patient';
  let touched = false;

  function close() {
    dispatch('close');
  }

  function onOverlay(e) {
    // close only when clicking the overlay (not the dialog)
    if (e.target === e.currentTarget) { close(); }
  }

  function onKey(e) {
    if (e.key === 'Escape') { close(); }
  }

  function submit(e) {
    e.preventDefault();
    touched = true;

    const payload = {
      name: name.trim(),
      email: email.trim(),
      role
    };

    if (!payload.name || !payload.email || !payload.role) {
      return; // simple required validation; style shows errors
    }

    dispatch('submit', payload);
  }

  onMount(() => {
    // reset fields whenever the modal is opened
    const unwatch = () => {};
    return unwatch;
  });
</script>

{#if open}
  <div class="overlay" on:click={onOverlay} on:keydown={onKey} tabindex="-1" style="position:fixed;inset:0;background:rgba(0,0,0,.45);display:flex;align-items:center;justify-content:center;z-index:80;">
    <div class="card" role="dialog" aria-modal="true" aria-labelledby="cu-title" style="width:min(720px,95%);padding:20px;border-radius:14px;">
      <div style="display:flex;align-items:center;justify-content:space-between;">
        <div>
          <h2 id="cu-title" style="margin:.25rem 0 .2rem 0;">Create New User</h2>
          <p class="muted" style="margin:0;">Add a new user to the system with appropriate role permissions.</p>
        </div>
        <button class="btn ghost" aria-label="Close create user dialog" on:click={close}>✕</button>
      </div>

      <form on:submit={submit} style="margin-top:14px;display:grid;gap:14px;">
        <div>
          <label for="cu-name" style="font-weight:700;">Full Name</label>
          <input
            id="cu-name"
            class="input"
            placeholder="Enter full name"
            bind:value={name}
            aria-invalid={touched && !name ? 'true' : 'false'}
            required
          />
          {#if touched && !name}
            <div class="muted" style="color:#b91c1c;margin-top:6px;">Name is required.</div>
          {/if}
        </div>

        <div>
          <label for="cu-email" style="font-weight:700;">Email</label>
          <input
            id="cu-email"
            class="input"
            type="email"
            placeholder="Enter email address"
            bind:value={email}
            aria-invalid={touched && !email ? 'true' : 'false'}
            required
          />
          {#if touched && !email}
            <div class="muted" style="color:#b91c1c;margin-top:6px;">Email is required.</div>
          {/if}
        </div>

        <div>
          <label for="cu-role" style="font-weight:700;">Role</label>
          <select id="cu-role" class="input" bind:value={role} required>
            <option value="patient">Patient</option>
            <option value="doctor">Doctor</option>
            <option value="admin">Admin</option>
          </select>
        </div>

        <div style="display:flex;justify-content:flex-end;margin-top:4px;">
          <button type="button" class="btn ghost" on:click={close} style="margin-right:8px;">Cancel</button>
          <button type="submit" class="btn primary">Create User</button>
        </div>
      </form>
    </div>
  </div>
{/if}
