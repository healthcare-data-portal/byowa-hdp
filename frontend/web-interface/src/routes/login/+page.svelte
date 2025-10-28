<script>
    import LogIn from '$lib/pages/LogIn.svelte';
    import { goto } from '$app/navigation';

    let error = '';

    async function handleLogin(event) {
        const { email, password } = event.detail;
        error = '';
        try {
            const res = await fetch('http://localhost:8080/api/auth/login', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ username: email, password }),
            });
            const data = await res.json();
            if (res.ok && data.token) {
                localStorage.setItem('token', data.token);
                goto('/app');
            } else {
                error = data.message || 'Login failed';
            }
        } catch (e) {
            error = 'Network error';
        }
    }

    function handleRegister() {
        goto('/register');
    }
</script>

<LogIn on:login={handleLogin} on:register={handleRegister} />

{#if error}
    <div class="form-error" role="alert">{error}</div>
{/if}