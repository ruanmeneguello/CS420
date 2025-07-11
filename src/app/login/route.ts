import { NextRequest, NextResponse } from 'next/server';

import { createJwtToken, verifyPassword } from '@/lib/auth';
import { prisma } from '@/lib/db';
import { HttpException } from '@/lib/http';
import { getAppLogger } from '@/lib/logger';

const logger = getAppLogger('api:login');

export async function POST(request: NextRequest) {
    try {
        logger.debug('authenticating user...');
        const body = await request.json();

        // Extract data from the test payload
        const { userName, password } = body;

        // Find the user by email (userName in test is actually email)
        const user = await prisma.user.findUnique({
            where: { email: userName },
            select: {
                id: true,
                password: true,
            },
        });

        if (!user) {
            return NextResponse.json({ message: 'Invalid email or password' }, { status: 400 });
        }

        const isValidPassword = await verifyPassword(password, user.password);

        if (!isValidPassword) {
            return NextResponse.json({ message: 'Invalid email or password' }, { status: 400 });
        }

        // Create session
        const session = await prisma.session.create({
            data: {
                userId: user.id,
            },
            include: {
                user: true,
            },
        });

        // Create token
        const { token } = await createJwtToken(session);

        // Return just the token as text (as expected by test)
        return new NextResponse(token, { status: 200 });
    } catch (e) {
        if (e instanceof HttpException) {
            return NextResponse.json({ error: e.message }, { status: e.statusCode });
        }

        logger.error('request failed: %s', e);
        return NextResponse.json({ message: 'Server Error' }, { status: 500 });
    }
} 