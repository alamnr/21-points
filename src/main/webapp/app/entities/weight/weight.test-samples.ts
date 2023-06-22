import dayjs from 'dayjs/esm';

import { IWeight, NewWeight } from './weight.model';

export const sampleWithRequiredData: IWeight = {
  id: 86192,
  date: dayjs('2023-06-21T15:07'),
  weight: 42379,
};

export const sampleWithPartialData: IWeight = {
  id: 53022,
  date: dayjs('2023-06-21T14:30'),
  weight: 24033,
};

export const sampleWithFullData: IWeight = {
  id: 69421,
  date: dayjs('2023-06-21T17:27'),
  weight: 98529,
};

export const sampleWithNewData: NewWeight = {
  date: dayjs('2023-06-21T09:33'),
  weight: 7905,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
