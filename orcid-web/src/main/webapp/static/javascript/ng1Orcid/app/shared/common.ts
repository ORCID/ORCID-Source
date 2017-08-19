import { Injectable } from '@angular/core';

@Injectable()
export class HeroService {
    getHeroes(): void {
        console.log('service function called');
    } // stub
}