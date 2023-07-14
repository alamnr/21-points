import dayjs from 'dayjs/esm';
import { IUser } from 'app/entities/user/user.model';

export interface IBloodPressure {
  id: number;
  date?: dayjs.Dayjs | null;
  systolic?: number | null;
  diastolic?: number | null;
  user?: Pick<IUser, 'id' | 'login'> | null;
}

export interface IBloodPressureByPeriod {
  period: string;
  readings: Array<IBloodPressure>;
}

export type NewBloodPressure = Omit<IBloodPressure, 'id'> & { id: null };
