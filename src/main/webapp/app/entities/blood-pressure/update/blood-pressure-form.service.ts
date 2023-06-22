import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IBloodPressure, NewBloodPressure } from '../blood-pressure.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IBloodPressure for edit and NewBloodPressureFormGroupInput for create.
 */
type BloodPressureFormGroupInput = IBloodPressure | PartialWithRequiredKeyOf<NewBloodPressure>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IBloodPressure | NewBloodPressure> = Omit<T, 'date'> & {
  date?: string | null;
};

type BloodPressureFormRawValue = FormValueOf<IBloodPressure>;

type NewBloodPressureFormRawValue = FormValueOf<NewBloodPressure>;

type BloodPressureFormDefaults = Pick<NewBloodPressure, 'id' | 'date'>;

type BloodPressureFormGroupContent = {
  id: FormControl<BloodPressureFormRawValue['id'] | NewBloodPressure['id']>;
  date: FormControl<BloodPressureFormRawValue['date']>;
  systolic: FormControl<BloodPressureFormRawValue['systolic']>;
  diastolic: FormControl<BloodPressureFormRawValue['diastolic']>;
  user: FormControl<BloodPressureFormRawValue['user']>;
};

export type BloodPressureFormGroup = FormGroup<BloodPressureFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class BloodPressureFormService {
  createBloodPressureFormGroup(bloodPressure: BloodPressureFormGroupInput = { id: null }): BloodPressureFormGroup {
    const bloodPressureRawValue = this.convertBloodPressureToBloodPressureRawValue({
      ...this.getFormDefaults(),
      ...bloodPressure,
    });
    return new FormGroup<BloodPressureFormGroupContent>({
      id: new FormControl(
        { value: bloodPressureRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      date: new FormControl(bloodPressureRawValue.date, {
        validators: [Validators.required],
      }),
      systolic: new FormControl(bloodPressureRawValue.systolic, {
        validators: [Validators.required],
      }),
      diastolic: new FormControl(bloodPressureRawValue.diastolic, {
        validators: [Validators.required],
      }),
      user: new FormControl(bloodPressureRawValue.user),
    });
  }

  getBloodPressure(form: BloodPressureFormGroup): IBloodPressure | NewBloodPressure {
    return this.convertBloodPressureRawValueToBloodPressure(form.getRawValue() as BloodPressureFormRawValue | NewBloodPressureFormRawValue);
  }

  resetForm(form: BloodPressureFormGroup, bloodPressure: BloodPressureFormGroupInput): void {
    const bloodPressureRawValue = this.convertBloodPressureToBloodPressureRawValue({ ...this.getFormDefaults(), ...bloodPressure });
    form.reset(
      {
        ...bloodPressureRawValue,
        id: { value: bloodPressureRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): BloodPressureFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      date: currentTime,
    };
  }

  private convertBloodPressureRawValueToBloodPressure(
    rawBloodPressure: BloodPressureFormRawValue | NewBloodPressureFormRawValue
  ): IBloodPressure | NewBloodPressure {
    return {
      ...rawBloodPressure,
      date: dayjs(rawBloodPressure.date, DATE_TIME_FORMAT),
    };
  }

  private convertBloodPressureToBloodPressureRawValue(
    bloodPressure: IBloodPressure | (Partial<NewBloodPressure> & BloodPressureFormDefaults)
  ): BloodPressureFormRawValue | PartialWithRequiredKeyOf<NewBloodPressureFormRawValue> {
    return {
      ...bloodPressure,
      date: bloodPressure.date ? bloodPressure.date.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
