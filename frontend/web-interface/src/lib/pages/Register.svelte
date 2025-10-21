<script lang="ts">
    import { createEventDispatcher } from 'svelte';
    import Icon from '@iconify/svelte';
    const dispatch = createEventDispatcher();

    let name = '';
    let email = '';
    let password = '';
    let confirm = '';
    let showPassword = false;
    let showConfirm = false;
    let agreed = false;
    let error = '';

    function onSubmit(e: SubmitEvent) {
        e.preventDefault();
        error = '';
        if (!name.trim()) { error = 'Please enter your full name.'; return; }
        if (!/^\S+@\S+\.\S+$/.test(email)) { error = 'Please enter a valid email.'; return; }
        if (password.length < 8) { error = 'Password must be at least 8 characters.'; return; }
        if (password !== confirm) { error = 'Passwords do not match.'; return; }
        if (!agreed) { error = 'Please agree to the Terms and Privacy Policy.'; return; }
        dispatch('submit', { name, email, password });
    }

    function cancel() {
        dispatch('cancel');
    }
</script>

<section class="auth-center">
    <div class="auth-card panel">
        <h1 class="auth-title">Create your account</h1>
        <p class="subtitle muted">Join the Healthcare Data Portal</p>
        <form class="form-stack" autocomplete="on" on:submit={onSubmit}>
            <div class="field">
                <label for="name">Full name</label>
                <div class="input-group">
                    <span class="ix"><Icon icon="lucide:user" width="18" /></span>
                    <input id="name" class="input has-icon" type="text" bind:value={name} placeholder="Jane Doe" required />
                </div>
            </div>
            <div class="field">
                <label for="email">Email</label>
                <div class="input-group">
                    <span class="ix"><Icon icon="lucide:mail" width="18" /></span>
                    <input id="email" class="input has-icon" type="email" bind:value={email} placeholder="you@example.com" autocomplete="email" required />
                </div>
            </div>
            <div class="field">
                <label for="password">Password</label>
                <div class="input-group">
                    <span class="ix"><Icon icon="lucide:lock" width="18" /></span>
                    <input id="password" class="input has-icon" type={showPassword ? 'text' : 'password'} bind:value={password} placeholder="At least 8 characters" autocomplete="new-password" required />
                    <button type="button" class="icon-btn toggle-eye" aria-label={showPassword ? 'Hide password' : 'Show password'} on:click={() => (showPassword = !showPassword)}>
                        <Icon icon={showPassword ? 'lucide:eye-off' : 'lucide:eye'} width="18" />
                    </button>
                </div>
            </div>
            <div class="field">
                <label for="confirm">Confirm password</label>
                <div class="input-group">
                    <span class="ix"><Icon icon="lucide:lock" width="18" /></span>
                    <input id="confirm" class="input has-icon" type={showConfirm ? 'text' : 'password'} bind:value={confirm} placeholder="Repeat your password" autocomplete="new-password" required />
                    <button type="button" class="icon-btn toggle-eye" aria-label={showConfirm ? 'Hide password' : 'Show password'} on:click={() => (showConfirm = !showConfirm)}>
                        <Icon icon={showConfirm ? 'lucide:eye-off' : 'lucide:eye'} width="18" />
                    </button>
                </div>
            </div>
            <label class="agree-row">
                <input type="checkbox" bind:checked={agreed} />
                <span class="muted">I agree to the Terms and Privacy Policy</span>
            </label>
            {#if error}
                <div class="form-error" role="alert">{error}</div>
            {/if}
            <button class="btn primary block btn-lg" type="submit">Create account</button>
            <div class="divider"></div>
            <button class="btn outline block" type="button" on:click={cancel}>Back to Sign In</button>
        </form>
    </div>
</section>
