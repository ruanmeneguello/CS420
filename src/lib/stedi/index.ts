import { headers } from 'next/headers';

export async function getStediToken(): Promise<string | null> {
    // Get the session token from headers (case-insensitive)
    const requestHeaders = await headers();
    return requestHeaders.get('suresteps-session-token');
}
