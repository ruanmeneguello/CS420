import { NextRequest, NextResponse } from 'next/server';

export async function POST(request: NextRequest) {
    try {
        // Get the request body
        const body = await request.json();

        // Forward the request to the old API at dev.stedi.me
        const response = await fetch('https://dev.stedi.me/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(body),
        });

        // Return the session token as text if successful
        if (response.ok) {
            const token = await response.text();
            return new NextResponse(token, { status: 200 });
        } else {
            return new NextResponse(null, { status: response.status });
        }
    } catch (_error) {
        // Handle any network or parsing errors
        return NextResponse.json({ error: 'Server Error' }, { status: 500 });
    }
}
