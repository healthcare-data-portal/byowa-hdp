export type Role = 'ADMIN' | 'DOCTOR' | 'PATIENT' | null;

function b64urlDecode(s: string): string {
	s = s.replace(/-/g, '+').replace(/_/g, '/');
	const pad = s.length % 4 ? 4 - (s.length % 4) : 0;
	const base64 = s + '='.repeat(pad);

	const a = (globalThis as any).atob as ((b64: string) => string) | undefined;
	if (!a) return '';

	const bin = a(base64);
	return decodeURIComponent(
		Array.from(bin, c => '%' + c.charCodeAt(0).toString(16).padStart(2, '0')).join('')
	);
}

export function getRoleFromToken(token: string): Role {
	try {
		const payloadRaw = token.split('.')[1] ?? '';
		const json = b64urlDecode(payloadRaw);
		if (!json) return null;
		const payload = JSON.parse(json);
		const raw = (payload.role ?? payload.roles ?? '').toString().toUpperCase();
		const norm = raw.replace(/^ROLE_/, '');
		return (['ADMIN', 'DOCTOR', 'PATIENT'] as const).includes(norm as any)
			? (norm as Role)
			: null;
	} catch {
		return null;
	}
}

export function pathForRole(r: Role) {
    if (r === 'ADMIN') return '/admin';
    if (r === 'DOCTOR') return '/doctor';
    if (r === 'PATIENT') return '/patient';
    return '/login';
}

// --- Helpers to extract user info (email/name) from JWT ---
export type JwtPayload = Record<string, any> | null;

export function parseJwtPayload(token: string): JwtPayload {
    try {
        const payloadRaw = token.split('.')[1] ?? '';
        const json = b64urlDecode(payloadRaw);
        if (!json) return null;
        return JSON.parse(json);
    } catch {
        return null;
    }
}

export function getEmailFromToken(token: string): string {
    const p = parseJwtPayload(token);
    // Spring JWT sets subject (sub) to username (we use email as username)
    return (
        (p?.sub as string) ||
        (p?.email as string) ||
        (p?.username as string) ||
        ''
    );
}

function capitalizeWord(s: string) {
    if (!s) return s;
    return s.charAt(0).toUpperCase() + s.slice(1).toLowerCase();
}

function nameFromEmail(email: string): string {
    if (!email) return '';
    const local = email.split('@')[0] || '';
    // Split by common separators and numbers
    const parts = local
        .replace(/[^a-zA-Z]+/g, ' ')
        .split(' ')
        .filter(Boolean)
        .slice(0, 3);
    if (!parts.length) return '';
    return parts.map(capitalizeWord).join(' ');
}

export function getDisplayNameFromToken(token: string): string {
    const p = parseJwtPayload(token);
    const given = (p?.given_name as string) || (p?.firstName as string);
    const family = (p?.family_name as string) || (p?.lastName as string);
    const full =
        (p?.name as string) ||
        (p?.fullName as string) ||
        (given && family ? `${given} ${family}` : given || family || '');
    if (full && full.trim()) return full.trim();
    return nameFromEmail(getEmailFromToken(token));
}

export function getUserInfoFromToken(token: string): { name: string; email: string } {
    const email = getEmailFromToken(token);
    const name = getDisplayNameFromToken(token);
    return { name, email };
}
