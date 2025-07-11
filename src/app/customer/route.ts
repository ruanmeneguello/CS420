import { NextRequest, NextResponse } from 'next/server';

import { getStediToken } from '@/lib/stedi';

export async function POST(request: NextRequest) {
    try {
        // Get the request body
        const body = await request.json();

        // Debug: log all headers
        console.log('Request headers:', Object.fromEntries(request.headers.entries()));

        // Get the session token from headers (case-insensitive)
        let sessionToken = await getStediToken();

        if (!sessionToken) {
            return NextResponse.json({ error: 'Missing session token' }, { status: 401 });
        }

        // Forward the request to the old API at dev.stedi.me
        const response = await fetch('https://dev.stedi.me/customer', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'suresteps.session.token': sessionToken,
            },
            body: JSON.stringify(body),
        });

        if (response.ok) {
            return new NextResponse(null, { status: 200 });
        } else {
            const errorText = await response.text();
            console.error('Customer API error:', response.status, errorText);
            return NextResponse.json({ error: errorText }, { status: response.status });
        }
    } catch (error) {
        console.error('Request failed:', error);
        return NextResponse.json({ error: 'Server Error' }, { status: 500 });
    }
}
