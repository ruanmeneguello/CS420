import { NextRequest, NextResponse } from 'next/server';

export async function POST(request: NextRequest) {
    try {
        // Get the request body
        const body = await request.json();
        
        // Get the session token from headers
        const sessionToken = request.headers.get('suresteps.session.token');
        
        // Forward the request to the old API at dev.stedi.me
        const response = await fetch('https://dev.stedi.me/customer', {
            method: 'POST',
            headers: { 
                'Content-Type': 'application/json',
                'suresteps.session.token': sessionToken || '',
            },
            body: JSON.stringify(body),
        });
        
        // Return status 200 if successful, otherwise return the error status
        if (response.ok) {
            return new NextResponse(null, { status: 200 });
        } else {
            return new NextResponse(null, { status: response.status });
        }
    } catch (error) {
        // Handle any network or parsing errors
        return NextResponse.json({ error: 'Server Error' }, { status: 500 });
    }
}
