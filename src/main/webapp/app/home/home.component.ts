import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

import { AccountService } from 'app/core/auth/account.service';
import { Account } from 'app/core/auth/account.model';
import { PointsService } from 'app/entities/points/service/points.service';
import { IPointsPerWeek } from 'app/entities/points/points.model';
import { default as dayjs } from 'dayjs/esm';
import { IPreferences } from 'app/entities/preferences/preferences.model';
import { PreferencesService } from 'app/entities/preferences/service/preferences.service';
import { BloodPressureService } from 'app/entities/blood-pressure/service/blood-pressure.service';
import { IBloodPressure, IBloodPressureByPeriod } from 'app/entities/blood-pressure/blood-pressure.model';

import { ChartConfiguration, ChartOptions } from 'chart.js';

@Component({
  selector: 'jhi-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss'],
})
export class HomeComponent implements OnInit, OnDestroy {
  account: Account | null = null;
  pointsThisWeek: IPointsPerWeek = { points: 0 };
  pointsPercentage?: number;
  preferences!: IPreferences;
  bpReadings!: IBloodPressureByPeriod;
  bpOptions!: ChartOptions<'line'>;
  bpData!: ChartConfiguration<'line'>['data'];

  private readonly destroy$ = new Subject<void>();

  constructor(
    private accountService: AccountService,
    private router: Router,
    private pointsService: PointsService,
    private preferencesService: PreferencesService,
    private bloodPressureService: BloodPressureService
  ) {}

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
    // Get preferences
    this.preferencesService.user().subscribe((preferences: any) => {
      this.preferences = preferences.body;
      // Get points for the current week
      this.pointsService.thisWeek().subscribe(response => {
        if (response.body) {
          this.pointsThisWeek = response.body;
          this.pointsPercentage = (this.pointsThisWeek.points / 21) * 100;
          // calculate success, warning, or danger
          if (this.pointsThisWeek.points >= preferences.weeklyGoal) {
            this.pointsThisWeek.progress = 'success';
          } else if (this.pointsThisWeek.points < 10) {
            this.pointsThisWeek.progress = 'danger';
          } else if (this.pointsThisWeek.points > 10 && this.pointsThisWeek.points < preferences.weeklyGoal) {
            this.pointsThisWeek.progress = 'warning';
          }
        }
      });
    });

    // Get blood pressure readings for the last 30 days
    this.bloodPressureService.last30Days().subscribe((bpReadings: any) => {
      bpReadings = bpReadings.body;
      this.bpReadings = bpReadings;
      if (bpReadings.readings.length) {
        this.bpOptions = {
          plugins: {
            legend: { display: true },
            title: {
              display: true,
              text: bpReadings.period,
            },
          },
          scales: {
            y: {
              beginAtZero: false,
            },
            x: {
              beginAtZero: false,
            },
          },
        };
        const labels: any = [];
        const systolics: any = [];
        const diastolics: any = [];
        const upperValues: any = [];
        const lowerValues: any = [];
        bpReadings.readings.forEach((item: IBloodPressure) => {
          const timestamp = dayjs(item.date).format('MMM DD');
          labels.push(timestamp);
          systolics.push({
            x: timestamp,
            y: item.systolic,
          });
          diastolics.push({
            x: timestamp,
            y: item.diastolic,
          });
          upperValues.push(item.systolic);
          lowerValues.push(item.diastolic);
        });
        const datasets = [
          {
            data: systolics,
            label: 'Systolic',
          },
          {
            data: diastolics,
            label: 'Diastolic',
          },
        ];
        this.bpData = {
          labels,
          datasets,
        };
        // set y scale to be 10 more than max and min
        this.bpOptions.scales = {
          y: {
            max: Math.max(...upperValues) + 10,
            min: Math.min(...lowerValues) - 10,
          },
        };
        // show both systolic and diastolic on hover
        this.bpOptions.interaction = {
          mode: 'index',
          intersect: false,
        };
      } else {
        this.bpReadings.readings = [];
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
