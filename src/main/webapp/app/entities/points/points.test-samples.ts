import dayjs from 'dayjs/esm';

import { IPoints, NewPoints } from './points.model';

export const sampleWithRequiredData: IPoints = {
  id: 33269,
  date: dayjs('2023-06-21'),
};

export const sampleWithPartialData: IPoints = {
  id: 14659,
  date: dayjs('2023-06-21'),
  alcohol: 99708,
  notes: 'Isle redundant Concrete',
};

export const sampleWithFullData: IPoints = {
  id: 45763,
  date: dayjs('2023-06-21'),
  exercise: 94318,
  meals: 34931,
  alcohol: 5279,
  notes: 'invoice drive Borders',
};

export const sampleWithNewData: NewPoints = {
  date: dayjs('2023-06-21'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
