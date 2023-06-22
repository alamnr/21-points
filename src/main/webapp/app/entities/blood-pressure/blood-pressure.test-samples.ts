import dayjs from 'dayjs/esm';

import { IBloodPressure, NewBloodPressure } from './blood-pressure.model';

export const sampleWithRequiredData: IBloodPressure = {
  id: 82246,
  date: dayjs('2023-06-22T02:06'),
  systolic: 50366,
  diastolic: 28757,
};

export const sampleWithPartialData: IBloodPressure = {
  id: 37469,
  date: dayjs('2023-06-22T00:57'),
  systolic: 94935,
  diastolic: 2497,
};

export const sampleWithFullData: IBloodPressure = {
  id: 74963,
  date: dayjs('2023-06-22T02:33'),
  systolic: 81126,
  diastolic: 83758,
};

export const sampleWithNewData: NewBloodPressure = {
  date: dayjs('2023-06-21T07:46'),
  systolic: 64105,
  diastolic: 1733,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
