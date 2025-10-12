<script>
    import RegisterForm from '$lib/pages/Register.svelte';
    import { goto } from '$app/navigation';

    let error = '';

    async function handleRegister(event) {
        const { email, password } = event.detail;
        error = '';
        try {
            const res = await fetch('http://localhost:8080/api/auth/register', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ username: email, password }),
            });
            const data = await res.json();
            if (res.ok && data.token) {
                localStorage.setItem('token', data.token);
                goto('/app');
            } else {
                error = data.message || 'Registration failed';
            }
        } catch (e) {
            error = 'Network error';
        }
    }

    function handleCancel() {
        goto('/');
    }
</script>

<RegisterForm on:submit={handleRegister} on:cancel={handleCancel} />

{#if error}
    <div class="form-error" role="alert">{error}</div>
{/if}
