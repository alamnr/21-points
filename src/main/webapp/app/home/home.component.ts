import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

import { AccountService } from 'app/core/auth/account.service';
import { Account } from 'app/core/auth/account.model';
import { PointsService } from 'app/entities/points/service/points.service';
import { IPointsPerWeek } from 'app/entities/points/points.model';

@Component({
  selector: 'jhi-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss'],
})
export class HomeComponent implements OnInit, OnDestroy {
  account: Account | null = null;
  pointsThisWeek: IPointsPerWeek = { points: 0 };
  pointsPercentage?: number;

  private readonly destroy$ = new Subject<void>();

  constructor(private accountService: AccountService, private router: Router, private pointsService: PointsService) {}

  ngOnInit(): void {
    this.accountService
      .getAuthenticationState()
      .pipe(takeUntil(this.destroy$))
      .subscribe(account => {
        this.account = account;
        this.getUserData();
      });
  }

  getUserData(): void {
    // Get points for the current week
    this.pointsService.thisWeek().subscribe(response => {
      if (response.body) {
        this.pointsThisWeek = response.body;
        this.pointsPercentage = (this.pointsThisWeek.points / 21) * 100;
      }
    });
  }

  login(): void {
    this.router.navigate(['/login']);
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
