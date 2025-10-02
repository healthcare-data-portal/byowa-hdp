<script>
    import Login from '$lib/pages/Login.svelte';
    import { goto } from '$app/navigation';

    function handleLogin() {
        goto('/app');
    }

    const handleRegister = () => goto('/register');
    const handleForgot   = () => goto('/forgot');
</script>

<Login
        on:login={handleLogin}
        on:register={handleRegister}
        on:forgotPassword={handleForgot}
/>
