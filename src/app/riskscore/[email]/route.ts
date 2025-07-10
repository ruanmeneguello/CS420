import { NextRequest, NextResponse } from 'next/server';

type RouteParams = {
    email: string;
};

type PageProps = {
    params: Promise<RouteParams>;
};

export async function GET(request: NextRequest, { params }: PageProps) {
    try {
        const { email } = await params;
        
        // Get the session token from headers
        const sessionToken = request.headers.get('suresteps.session.token');
        
        // Forward the request to the old API at dev.stedi.me
        const response = await fetch(`https://dev.stedi.me/riskscore/${email}`, {
            method: 'GET',
            headers: { 
                'suresteps.session.token': sessionToken || '',
            },
        });
        
        // Return the risk score JSON if successful
        if (response.ok) {
            const result = await response.json();
            return NextResponse.json(result);
        } else {
            return new NextResponse(null, { status: response.status });
        }
    } catch (error) {
        // Handle any network or parsing errors
        return NextResponse.json({ error: 'Server Error' }, { status: 500 });
    }
}
