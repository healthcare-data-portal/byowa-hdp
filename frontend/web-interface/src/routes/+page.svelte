<script>
    import { goto } from '$app/navigation';

    import Login from '$lib/pages/Login.svelte';
    import PatientView from '$lib/pages/patientView.svelte';
    import AdminView from '$lib/pages/adminView.svelte';
    import DoctorView from '$lib/pages/doctorView.svelte';

    let view = 'login';

    const go = (v) => { view = v; };

    function handleLogin() { goto('/app'); }
    const handleRegister = () => goto('/register');
    const handleForgot   = () => goto('/forgot');

    const VIEWS = {
        patient: PatientView,
        admin: AdminView,
        doctor: DoctorView
    };

    $: Current = VIEWS[view];
</script>

<nav style="padding:8px">
    <button type="button" on:click={() => go('patient')}>patientView</button><br>
    <button type="button" on:click={() => go('admin')}>adminView</button><br>
    <button type="button" on:click={() => go('login')}>logIn</button><br>
    <button type="button" on:click={() => go('doctor')}>doctorView</button>
</nav>

<hr />

{#key view}
    {#if view === 'login'}
        <Login
                on:login={handleLogin}
                on:register={handleRegister}
                on:forgotPassword={handleForgot}
        />
    {:else if Current}
        <svelte:component this={Current} />
    {:else}
        <p>Unknown view: {view}</p>
    {/if}
{/key}
