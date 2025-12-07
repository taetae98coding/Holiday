# Holiday

한국의 공휴일 정보를 제공하는 프로젝트입니다.

## 개요

이 프로젝트는 한국 행정안전부의 공공데이터포털 OpenAPI를 통해 공휴일 정보를 수집하고, JSON 형식으로 제공합니다. GitHub Actions를 통해 매시간 자동으로 업데이트됩니다.

## 기능

- 한국의 공휴일 정보 수집 및 제공
- 연도별, 월별 공휴일 정보를 JSON 형식으로 제공
- GitHub Pages를 통한 API 제공
- 자동 업데이트 (매 시간)

## 데이터 형식

공휴일 정보는 `docs/holiday/` 디렉토리에 다음과 같은 형식으로 저장됩니다:

- `{year}.json`: 연도별 공휴일 정보
- `{year}-{month}.json`: 월별 공휴일 정보

## API

다음과 같은 형태로 API를 호출할 수 있습니다:

- 연도별 공휴일 정보: `https://taetae98coding.github.io/Holiday/holiday/{year}.json`
  - 예시: `https://taetae98coding.github.io/Holiday/holiday/2025.json`
- 월별 공휴일 정보: `https://taetae98coding.github.io/Holiday/holiday/{year}-{month}.json`
  - 예시: `https://taetae98coding.github.io/Holiday/holiday/2025-01.json`

## GitHub Pages

👉 **[GitHub Pages로 이동](https://taetae98coding.github.io/Holiday/)**
