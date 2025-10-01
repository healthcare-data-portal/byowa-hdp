<script>
  import PatientView from '$lib/pages/patientView.svelte';
  import AdminView from '$lib/pages/adminView.svelte';
  import DoctorView from '$lib/pages/doctorView.svelte';
  import LogIn from '$lib/pages/logIn.svelte';

  let view = ''; // '', 'patient', 'admin', 'doctor', 'login'
  const go = (v) => { view = v; };
</script>

<a href="#" on:click|preventDefault={() => go('patient')}>patientView</a><br>
<a href="#" on:click|preventDefault={() => go('admin')}>adminView</a><br>
<a href="#" on:click|preventDefault={() => go('login')}>logIn</a><br>
<a href="#" on:click|preventDefault={() => go('doctor')}>doctorView</a>

<hr>

{#if view === 'patient'}
  <PatientView />
{:else if view === 'admin'}
  <AdminView />
{:else if view === 'login'}
  <LogIn />
{:else if view === 'doctor'}
  <DoctorView />
{:else}
  <p>Choose a page above.</p>
{/if}
