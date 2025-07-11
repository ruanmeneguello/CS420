import { NextRequest, NextResponse } from 'next/server';

import { hashPassword } from '@/lib/auth';
import { prisma } from '@/lib/db';
import { HttpException } from '@/lib/http';
import { getAppLogger } from '@/lib/logger';

const logger = getAppLogger('api:user');

export async function POST(request: NextRequest) {
    try {
        logger.debug('creating user...');
        let body;
        try {
            body = await request.json();
        } catch (e) {
            return NextResponse.json({ message: 'Invalid or missing JSON body' }, { status: 400 });
        }

        // Extract data from the test payload
        const { userName, email, phone, region, birthDate, password, verifyPassword } = body;

        // Validate password match
        if (password !== verifyPassword) {
            return NextResponse.json({ message: 'Passwords do not match' }, { status: 400 });
        }

        // Check if user already exists
        const existingUser = await prisma.user.findUnique({
            where: { email },
            select: { id: true },
        });

        if (existingUser) {
            return NextResponse.json({ message: 'User already exists' }, { status: 409 });
        }

        // Hash password
        const hashedPassword = await hashPassword(password);

        // Create user
        const user = await prisma.user.create({
            data: {
                email,
                phone,
                firstName: userName.split('@')[0], // Use email prefix as firstName
                lastName: 'Test',
                dateOfBirth: new Date(birthDate),
                password: hashedPassword,
            },
        });

        return NextResponse.json({ id: user.id, email: user.email }, { status: 200 });
    } catch (e) {
        if (e instanceof HttpException) {
            return NextResponse.json({ error: e.message }, { status: e.statusCode });
        }

        logger.error('request failed: %s', e);
        return NextResponse.json({ message: 'Server Error' }, { status: 500 });
    }
} 